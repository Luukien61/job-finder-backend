package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.request.ReportCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.ReportResponse;
import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import com.kienluu.jobfinderbackend.service.implement.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/{jobId}")
    public ResponseEntity<?> reportJob(
            @PathVariable Long jobId,
            @RequestParam String userId,
            @RequestBody String reason) {
        try {
            // Xử lý tạo report
            ReportEntity report = reportService.createReport(userId, jobId, reason);
            return ResponseEntity.ok(report);
        } catch (IllegalStateException ex) {
            // Xử lý lỗi do ứng viên đã tố cáo bài đăng này
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Xử lý lỗi do jobId không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            // Xử lý lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
