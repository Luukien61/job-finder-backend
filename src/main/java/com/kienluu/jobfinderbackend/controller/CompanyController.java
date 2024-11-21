package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.service.implement.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping()
    List<CompanyEntity> getCompanies(){
        return companyService.getCompanies();
    }


    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable String companyId,
            @RequestBody UpdateCompanyRequest request) {
        try {
            CompanyResponse updatedCompany = companyService.updateCompany(companyId, request);
            return ResponseEntity.ok(updatedCompany);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
