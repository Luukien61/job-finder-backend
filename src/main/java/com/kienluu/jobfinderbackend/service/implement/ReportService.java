package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.response.ReportDto;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.ReportEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.ReportStatus;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.repository.ReportRepository;
import com.kienluu.jobfinderbackend.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ReportRepository reportRepository;
    private final JobRepository jobRepository;
    private final CustomMapper mapper;

    public ReportDto createReport(String userId, Long jobId, String reason) {
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
        report = reportRepository.save(report);
        return mapper.toReportResponse(report);
    }
}
