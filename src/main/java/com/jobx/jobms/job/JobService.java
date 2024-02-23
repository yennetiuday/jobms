package com.jobx.jobms.job;

import com.jobx.jobms.job.dto.JobDTO;

import java.util.List;

public interface JobService {
    List<JobDTO> findAll();
    void createJob(Job job);

    JobDTO getJobById(Long id);

    Boolean deleteJobById(Long id);

    boolean updateJob(Long id, Job updatedJob);
}
