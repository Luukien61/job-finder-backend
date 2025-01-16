package com.kienluu.jobfinderbackend.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.json.JsonData;
import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import com.kienluu.jobfinderbackend.elasticsearch.event.BanJobByCompanyEvent;
import com.kienluu.jobfinderbackend.elasticsearch.event.CompanyUpdateEvent;
import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.elasticsearch.repository.JobSearchRepository;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.event.CompanyTierChange;
import com.kienluu.jobfinderbackend.event.UserSearchEvent;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ScriptType;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobSearchService {
    private final JobSearchRepository jobSearchRepository;
    private final JobRepository jobRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ApplicationEventPublisher eventPublisher;


    private final String PAINLESS = "painless";
    private final String INDEX = "jobs";

    @Value("${stripe.checkout.plan.ultimate}")
    private String ULTIMATE_PLAN;
    @Value("${stripe.checkout.plan.pro}")
    private String PRO_PLAN;

    @EventListener
    public void handleJobChange(JobChangedEvent event) {
        try {
            JobEntity job = event.getJob();
            EvenType action = event.getAction();
            switch (action) {
                case EvenType.CREATED:
                case EvenType.UPDATED:
                    JobDocument jobDocument = JobDocument.builder()
                            .title(job.getTitle())
                            .id(job.getJobId().toString())
                            .location(job.getProvince())
                            .companyId(job.getCompany().getId())
                            .companyName(job.getCompany().getName())
                            .logo(job.getCompany().getLogo())
                            .experience(job.getExperience())
                            .createDate(job.getCreatedAt())
                            .expiryDate(job.getExpireDate())
                            .minSalary(job.getMinSalary())
                            .maxSalary(job.getMaxSalary())
                            .state(job.getState().toString())
                            .build();
                    jobSearchRepository.save(jobDocument);
                    log.info("Elasticsearch synchronized for job ID: {} - Action: {}", job.getJobId(), action);
                    break;
                case EvenType.DELETED:
                    jobSearchRepository.deleteById(job.getJobId().toString());
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void syncAllJobsToElasticsearch() {
        jobRepository.findAll().forEach(job -> {
            JobDocument jobDocument = JobDocument.builder()
                    .title(job.getTitle())
                    .id(job.getJobId().toString())
                    .location(job.getProvince())
                    .companyId(job.getCompany().getId())
                    .companyName(job.getCompany().getName())
                    .logo(job.getCompany().getLogo())
                    .experience(job.getExperience())
                    .createDate(job.getCreatedAt())
                    .expiryDate(job.getExpireDate())
                    .minSalary(job.getMinSalary())
                    .maxSalary(job.getMaxSalary())
                    .state(job.getState().toString())
                    .build();
            jobSearchRepository.save(jobDocument);
        });
    }

    public Page<JobDocument> searchJobs(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findByTitleContainingOrLocationContaining(keyword, pageable);
    }

    public Page<JobDocument> searchJobWithLocation(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findJobValidWithLocation("hồ Chí Minh", keyword, pageable);
    }

    public Page<JobDocument> searchJobWithLocationAndSalary(String keyword, int minSalary, int maxSalary, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findJobValidWithLocationAndSalary(keyword, "hồ Chí Minh", minSalary, maxSalary, pageable);
    }

    public Page<JobDocument> searchJobWithLocationAndSalaryAndExperience(String keyword, int minSalary, int maxSalary, int experience, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findJobValidWithLocationAndSalaryAndExperience(keyword, "hồ Chí Minh", minSalary, maxSalary, experience, pageable);
    }


    @EventListener(classes = BanJobByCompanyEvent.class)
    public void banJobsByCompany(BanJobByCompanyEvent event) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query -> query.bool(bool -> bool.must(must ->
                        must.term(term -> term.value(event.getCompanyId()).field("companyId"))
                )))
                .build();


        UpdateQuery updateQuery = UpdateQuery.builder(nativeQuery)
                .withScript("ctx._source.state='BANNED'")
                .withLang("painless")
                .withScriptType(ScriptType.INLINE)
                .build();

//        SearchHits<JobDocument> searchHits = elasticsearchOperations.search(nativeQuery, JobDocument.class);
//        List<JobDocument> jobDocuments = searchHits.stream()
//                .map(SearchHit::getContent)
//                .toList();
//        List<JobDocument> documents = jobSearchRepository.findAllByCompanyId(companyId);
//        log.info("Found {} jobs", documents.size());
        elasticsearchOperations.updateByQuery(updateQuery, IndexCoordinates.of("jobs"));
    }

    @EventListener(CompanyTierChange.class)
    public void onCompanyTierChange(CompanyTierChange event) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query -> query.term(term -> term.value(event.getCompanyId()).field("companyId")))
                .build();
        String script;
        if (event.getTier() != null) {
            script = "ctx._source.tier='" + event.getTier() + "'";
        } else {
            script = "ctx._source.tier= null";
        }
        UpdateQuery updateQuery = UpdateQuery.builder(nativeQuery)
                .withScript(script)
                .withScriptType(ScriptType.INLINE)
                .withLang(PAINLESS)
                .build();
        elasticsearchOperations.updateByQuery(updateQuery, IndexCoordinates.of(INDEX));
    }

    @EventListener(classes = CompanyUpdateEvent.class)
    public void companyUpdateEventListener(CompanyUpdateEvent event) throws IllegalAccessException {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query -> query.term(term -> term.value(event.getCompanyId()).field("companyId")))
                .build();
        Map<String, Object> fieldsToUpdate = new HashMap<>();
        for (Field field : event.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(event);
            if (value != null && !field.getName().equalsIgnoreCase("companyId")) {
                fieldsToUpdate.put(field.getName(), value);
            }
        }
        String script = fieldsToUpdate.keySet()
                .stream()
                .map(field -> "ctx._source." + field + " = params." + field)
                .reduce((f1, f2) -> f1 + "; " + f2)
                .orElse("");
        log.info("Script: {}", script);

        UpdateQuery updateQuery = UpdateQuery.builder(nativeQuery)
                .withScript(script)
                .withScriptType(ScriptType.INLINE)
                .withParams(fieldsToUpdate)
                .withLang("painless")
                .build();
        elasticsearchOperations.updateByQuery(updateQuery, IndexCoordinates.of("jobs"));
    }


    @SuppressWarnings("unchecked")
    public Page<JobDocument> searchJobs(
            @Nullable String keyword,
            @Nullable String location,
            @Nullable Integer minSalary,
            @Nullable Integer maxSalary,
            @Nullable Integer experience,
            int page, int size,
            String sort, String order,
            String userId
    ) {
        if (userId != null) {
            eventPublisher.publishEvent(new UserSearchEvent(userId, keyword));
        }
        Pageable pageable = PageRequest.of(page, size);
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query -> query.bool(bool -> {
                    if (StringUtils.hasText(keyword)) {
                        bool.must(must ->
                                must.multiMatch(multiMatch ->
                                        multiMatch.query(keyword)
                                                .fields(List.of("title^3", "companyName^2", "location"))
                                                .type(TextQueryType.MostFields)
                                )
                        );
                    }


                    bool.must(must ->
                            must.range(range ->
                                    range.field("expiryDate").gt(JsonData.of("now"))
                                            .format("yyyy-MM-dd")
                            )
                    );
                    bool.must(must -> must.term(term -> term.field("state").value("PENDING")));


                    if (StringUtils.hasText(location)) {
                        bool.filter(filter ->
                                filter.term(term ->
                                        term.field("location.keyword").value(location)
                                                .caseInsensitive(true)
                                )
                        );
                    }

                    if (maxSalary != null) {
                        bool.filter(filter ->
                                filter.range(range ->
                                        range.field("minSalary").lte(JsonData.of(maxSalary))
                                )
                        );
                    }

                    if (minSalary != null) {
                        bool.filter(filter ->
                                filter.range(range ->
                                        range.field("maxSalary").gte(JsonData.of(minSalary))
                                )
                        );
                    }

                    if (experience != null && experience > 0) {
                        if (experience <= 5) {
                            bool.filter(filter ->
                                    filter.term(term ->
                                            term.field("experience").value(experience)
                                    )
                            );
                        } else {
                            bool.filter(filter ->
                                    filter.range(range ->
                                            range.field("experience").gt(JsonData.of(experience))
                                    )
                            );
                        }

                    }

                    return bool;
                }))
                .withSort(sorts ->
                        sorts.field(builder -> {
                                    String sortByField = convertToCamelCase(sort);
                                    SortOrder orderBy = SortOrder.Asc;
                                    if (order.equalsIgnoreCase("desc")) {
                                        orderBy = SortOrder.Desc;
                                    }
                                    return builder.field(sortByField).order(orderBy);
                                }
                        ))
                .withPageable(pageable)
                .build();
        FunctionScore score1 = FunctionScore.of(func ->
                func.filter(filter -> filter
                                .term(term -> term
                                        .field("tier").value(ULTIMATE_PLAN)))
                        .weight(7.0));
        FunctionScore score2 = FunctionScore.of(func -> func
                .filter(filter -> filter
                        .term(term -> term
                                .field("tier").value(PRO_PLAN)))
                .weight(5.0));
        List<FunctionScore> functionScores = Arrays.asList(score1, score2);

        NativeQuery searchQuery2 = NativeQuery.builder()
                .withQuery(query -> query
                        .functionScore(functionScore -> functionScore
                                .query(query2 -> query2.bool(bool -> {
                                    if (StringUtils.hasText(keyword)) {
                                        bool.must(must ->
                                                must.multiMatch(multiMatch ->
                                                        multiMatch.query(keyword)
                                                                .fields(List.of("title^3", "companyName^2", "location"))
                                                                .type(TextQueryType.MostFields)
                                                )
                                        );
                                    }


                                    bool.must(must ->
                                            must.range(range ->
                                                    range.field("expiryDate").gt(JsonData.of("now"))
                                                            .format("yyyy-MM-dd")
                                            )
                                    );
                                    bool.must(must -> must.term(term -> term.field("state").value("PENDING")));


                                    if (StringUtils.hasText(location)) {
                                        bool.filter(filter ->
                                                filter.term(term ->
                                                        term.field("location.keyword").value(location)
                                                                .caseInsensitive(true)
                                                )
                                        );
                                    }

                                    if (maxSalary != null) {
                                        bool.filter(filter ->
                                                filter.range(range ->
                                                        range.field("minSalary").lte(JsonData.of(maxSalary))
                                                )
                                        );
                                    }

                                    if (minSalary != null) {
                                        bool.filter(filter ->
                                                filter.range(range ->
                                                        range.field("maxSalary").gte(JsonData.of(minSalary))
                                                )
                                        );
                                    }

                                    if (experience != null && experience > 0) {
                                        if (experience <= 5) {
                                            bool.filter(filter ->
                                                    filter.term(term ->
                                                            term.field("experience").value(experience)
                                                    )
                                            );
                                        } else {
                                            bool.filter(filter ->
                                                    filter.range(range ->
                                                            range.field("experience").gt(JsonData.of(experience))
                                                    )
                                            );
                                        }

                                    }

                                    return bool;
                                }))
                                .functions(functionScores)
                                .boostMode(FunctionBoostMode.Sum)
                                .scoreMode(FunctionScoreMode.Sum)
                        ))


                .withSort(sorts ->
                        sorts.field(builder -> {
                                    String sortByField = convertToCamelCase(sort);
                                    SortOrder orderBy = SortOrder.Asc;
                                    if (order.equalsIgnoreCase("desc")) {
                                        orderBy = SortOrder.Desc;
                                    }
                                    return builder.field(sortByField).order(orderBy);
                                }
                        ))
                .withPageable(pageable)
                .build();



        SearchHits<JobDocument> searchHits = elasticsearchOperations.search(searchQuery2, JobDocument.class);
        SearchPage<JobDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
        return (Page<JobDocument>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

    private String convertToCamelCase(String source) {
        // Chuyển snake_case hoặc kebab-case thành camelCase
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        for (char c : source.toCharArray()) {
            if (c == '-' || c == '_') {
                toUpperCase = true;
            } else if (toUpperCase) {
                result.append(Character.toUpperCase(c));
                toUpperCase = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

}
