package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.CompanyDto;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UpdateCompanyRequest;
import com.kienluu.jobfinderbackend.dto.response.CompanyCreateResponse;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponse;
import com.kienluu.jobfinderbackend.dto.response.CompanyResponsePage;
import com.kienluu.jobfinderbackend.dto.response.LoginResponse;
import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import com.kienluu.jobfinderbackend.service.ICompanyService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CompanyController {

    private final ICompanyService companyService;

    @GetMapping("/all")
    public ResponseEntity<List<CompanyEntity>> getCompanies() {
        try {
            List<CompanyEntity> companies = companyService.getAllCompanies();
            System.out.println("Companies in Controller: " + companies); // Kiểm tra log
            return ResponseEntity.ok(companies); // Trả về danh sách dù có rỗng
        } catch (Exception e) {
            System.err.println("Error in Controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }



    @GetMapping("/{companyId}")
    public ResponseEntity<Object> getCompany(@PathVariable String companyId){
        try{
            CompanyResponse response = companyService.getCompanyById(companyId);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Page<Object[]>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder)
    {
        try {
            Page<Object[]> companies = companyService.getCompanies(page, size, sortBy, sortOrder);
            return ResponseEntity.ok(companies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
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

    @PostMapping("/code")
    public ResponseEntity<Object> sendVerificationCode(@RequestBody MailTemplate mailTemplate) {
        try{
            String code = companyService.sendVerificationCode(mailTemplate);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<Object> createCompany(@RequestBody CompanyDto request){
        try{
            CompanyCreateResponse createdCompany = companyService.createCompany(request);
            return ResponseEntity.ok(createdCompany);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest){
        try{
            LoginResponse response = companyService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }
}
