package com.kienluu.jobfinderbackend.elasticsearch.service;

import com.kienluu.jobfinderbackend.elasticsearch.event.CompanyUpdateEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JobSearchServiceTest {

    private final JobSearchService jobSearchService = new JobSearchService(null, null, null,null);

    @Test
    void companyUpdateEventListener() throws IllegalAccessException {
//        CompanyUpdateEvent event = new CompanyUpdateEvent("company_123", "Luu Dinh Kien", "https://abc.com");
//        UpdateQuery result = jobSearchService.companyUpdateEventListener(event);
//        assertNotNull(result);
//        assertEquals("ctx._source.companyName = params.companyName; ctx._source.logo = params.logo", result.getScript());
//        Map<String, Object> params = result.getParams();
//        assert params != null;
//        assertEquals(2, params.size());
//        assertEquals("Luu Dinh Kien", params.get("companyName"));
//        assertEquals("https://abc.com", params.get("logo"));
    }
}