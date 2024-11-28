package com.kienluu.jobfinderbackend.elasticsearch.service;

import co.elastic.clients.json.JsonData;
import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import com.kienluu.jobfinderbackend.elasticsearch.event.EvenType;
import com.kienluu.jobfinderbackend.elasticsearch.event.JobChangedEvent;
import com.kienluu.jobfinderbackend.elasticsearch.repository.JobSearchRepository;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@AllArgsConstructor
public class JobSearchService {
    private JobSearchRepository jobSearchRepository;
    private JobRepository jobRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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
                            .location(job.getLocation())
                            .companyId(job.getCompany().getId())
                            .companyName(job.getCompany().getName())
                            .logo(job.getCompany().getLogo())
                            .experience(job.getExperience())
                            .expiryDate(job.getExpireDate())
                            .minSalary(job.getMinSalary())
                            .maxSalary(job.getMaxSalary())
                            .build();
                    jobSearchRepository.save(jobDocument);
                    log.info("Elasticsearch synchronized for job ID: {} - Action: {}", job.getJobId(), action);
                    break;
                case EvenType.DELETED:
                    jobSearchRepository.deleteById(job.getJobId().toString());
                    break;
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void syncAllJobsToElasticsearch() {
        jobRepository.findAll().forEach(job -> {
            JobDocument jobDocument = new JobDocument();
            jobDocument.setId(job.getJobId().toString());
            jobDocument.setTitle(job.getTitle());
            jobDocument.setLocation(job.getLocation());
            jobSearchRepository.save(jobDocument);
        });
    }

    public Page<JobDocument> searchJobs(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findByTitleContainingOrLocationContaining(keyword,pageable);
    }

    public Page<JobDocument> searchJobWithLocation(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findJobValidWithLocation("hồ Chí Minh",keyword,pageable);
    }

    public Page<JobDocument> searchJobWithLocationAndSalary(String keyword, int minSalary, int maxSalary,  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findJobValidWithLocationAndSalary(keyword,"hồ Chí Minh", minSalary, maxSalary,pageable);
    }
    public Page<JobDocument> searchJobWithLocationAndSalaryAndExperience(String keyword, int minSalary, int maxSalary, int experience,  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return jobSearchRepository.findJobValidWithLocationAndSalaryAndExperience(keyword,"hồ Chí Minh", minSalary, maxSalary,experience,pageable);
    }



    @SuppressWarnings("unchecked")
    public Page<JobDocument> searchJobs(
            @Nullable String location,
            @Nullable Integer minSalary,
            @Nullable Integer maxSalary,
            @Nullable Integer experience,
            int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query -> query.bool(bool -> {
                    bool.must(must ->
                            must.range(range ->
                                    range.field("expiryDate").gt(JsonData.of("now"))
                                            .format("yyyy-MM-dd")
                            )
                    );

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

                    if ( minSalary != null) {
                        bool.filter(filter ->
                                filter.range(range ->
                                        range.field("maxSalary").gte(JsonData.of(minSalary))
                                )
                        );
                    }

                    if (experience!=null) {
                        bool.filter(filter ->
                                filter.term(term ->
                                        term.field("experience").value(experience)
                                )
                        );
                    }
                    return bool;
                }))
                .withPageable(pageable)
                .build();
        log.info("Search query: {}", searchQuery);

        SearchHits<JobDocument> searchHits = elasticsearchOperations.search(searchQuery, JobDocument.class);
        SearchPage<JobDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, pageable);
        return (Page<JobDocument>) SearchHitSupport.unwrapSearchHits(searchPage);
    }
}
