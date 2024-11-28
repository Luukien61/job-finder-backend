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
                "filter": [
                  {
                    "range": {
                      "expiryDate": {
                        "gt": "now"
                      }
                    }
                  }
                ],
                "must": [
                  {
                    "multi_match": {
                      "query": "#{#title}",
                      "fields": ["title^3", "companyName^2", "location"],
                      "type": "best_fields"
                    }
                  }
                ]
              }
            }
            """)
    Page<JobDocument> findByTitleContainingOrLocationContaining(String title, Pageable pageable);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "multi_match": {
                      "query": "?1",
                      "fields": ["title^3", "companyName^2", "location"],
                      "type": "best_fields"
                    }
                  },
                  {
                    "range": {
                      "expiryDate": {
                        "gt": "now",
                        "format": "yyyy-MM-dd"
                      }
                    }
                  }
                ],
                "filter": {
                   "term": {
                      "location.keyword": {
                          "value": "?0",
                          "case_insensitive": true
                      }
                   }
                }
              }
            }
            """)
    Page<JobDocument> findJobValidWithLocation(String location, String title, Pageable pageable);

    @Query("""
            {
                  "bool": {
                    "must": [
                      {
                        "multi_match": {
                          "query": "?0",
                          "fields": [
                            "title^3",
                            "companyName^2",
                            "location"
                          ],
                          "type": "best_fields"
                        }
                      },
                      {
                        "range": {
                          "expiryDate": {
                            "gt": "now",
                            "format": "yyyy-MM-dd"
                          }
                        }
                      }
                    ],
                    "filter": [
                      {
                        "term": {
                          "location.keyword": {
                            "value": "?1",
                            "case_insensitive": true
                          }
                        }
                      },
                      {
                        "range": {
                          "maxSalary": {
                            "gte": "?2"
                          }
                        }
                      },
                      {
                        "range": {
                          "minSalary": {
                            "lte": "?3"
                          }
                        }
                      }
                    ]
                  }
                }
            """)
    Page<JobDocument> findJobValidWithLocationAndSalary(String title, String location, int minSalary, int maxSalary, Pageable pageable);

    @Query("""
            {
                   "bool": {
                     "must": [
                       {
                         "multi_match": {
                           "query": "?0",
                           "fields": [
                             "title^3",
                             "companyName^2",
                             "location"
                           ],
                           "type": "best_fields"
                         }
                       },
                       {
                         "range": {
                           "expiryDate": {
                             "gt": "now",
                             "format": "yyyy-MM-dd"
                           }
                         }
                       }
                     ],
                     "filter": [
                       {
                         "term": {
                           "location.keyword": {
                             "value": "?1",
                             "case_insensitive": true
                           }
                         }
                       },
                       {
                         "range": {
                           "maxSalary": {
                             "gte": "?2"
                           }
                         }
                       },
                       {
                         "range": {
                           "minSalary": {
                             "lte": "?3"
                           }
                         }
                       },
                       {
                         "term": {
                           "experience": "?4"
                         }
                       }
                     ]
                   }
                 }
            """)
    Page<JobDocument> findJobValidWithLocationAndSalaryAndExperience(String title, String location, int minSalary, int maxSalary, int experience, Pageable pageable);

}
