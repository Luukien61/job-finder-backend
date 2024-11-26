package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.request.ReportCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.ReportResponse;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.repository.ReportRepository;
import com.kienluu.jobfinderbackend.service.IReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ReportRepository reportRepository;
    private final JobRepository jobRepository;

    public ReportEntity createReport(String userId, Long jobId, String reason) {
        // Kiểm tra bài đăng có tồn tại không
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Kiểm tra xem user đã tố cáo bài đăng này chưa
        if (reportRepository.existsByJobIdAndUserId(jobId, userId)) {
            throw new IllegalStateException("Bạn đã tố cáo bài đăng này rồi.");
        }

        // Tạo report
        ReportEntity report = ReportEntity.builder()
                .userId(userId)
                .job(job)
                .rpReason(reason)
                .status(ReportStatus.PENDING)
                .build();

        return reportRepository.save(report);
    }
}
