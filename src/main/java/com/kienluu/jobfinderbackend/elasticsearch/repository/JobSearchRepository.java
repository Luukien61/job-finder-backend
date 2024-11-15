package com.kienluu.jobfinderbackend.elasticsearch.repository;

import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, String> {

    @Query("""
            {
              "bool": {
                     "should": [
                       {
                         "multi_match": {
                           "query": "#{#title}",
                           "fields": ["title", "location"]
                         }
                       }
                     ]
                   }
            }
            """)
    Page<JobDocument> findByTitleContainingOrLocationContaining(String title, String address,
                                                                Pageable pageable);
}
