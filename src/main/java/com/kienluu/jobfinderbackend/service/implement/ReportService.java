package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.ReportDTO;
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

    public ReportDTO createReport(ReportDTO reportDTO) {
        Long jobId = reportDTO.getJobId();
        String userId = reportDTO.getUserId();

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (reportRepository.existsByJobIdAndUserId(jobId, userId)) {
            throw new IllegalStateException("Bạn đã tố cáo bài đăng này rồi.");
        }

        ReportEntity report = ReportEntity.builder()
                .userId(userId)
                .job(job)
                .companyId(reportDTO.getCompanyId())
                .rpReason(reportDTO.getRpReason())
                .status(ReportStatus.PENDING)
                .build();
        report = reportRepository.save(report);
        return mapper.toReportResponse(report);
    }
}
