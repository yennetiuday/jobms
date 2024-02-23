package com.jobx.jobms.job.impl;

import com.jobx.jobms.job.Job;
import com.jobx.jobms.job.JobRepository;
import com.jobx.jobms.job.JobService;
import com.jobx.jobms.job.clients.CompanyClient;
import com.jobx.jobms.job.clients.ReviewClient;
import com.jobx.jobms.job.dto.JobDTO;
import com.jobx.jobms.job.external.Company;
import com.jobx.jobms.job.external.Review;
import com.jobx.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

//    @Autowired
//    private RestTemplate restTemplate;
    private JobRepository jobRepository;
    private CompanyClient companyClient;
    private ReviewClient reviewClient;

//    int attempt = 0;

    public JobServiceImpl(JobRepository jobRepository,
                          CompanyClient companyClient,
                          ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }

    @Override
//    @CircuitBreaker(name="companyBreaker",
//            fallbackMethod = "companyBreakerFallback")
//    @Retry(name="companyBreaker",
//            fallbackMethod = "companyBreakerFallback")
    @RateLimiter(name="companyBreaker",
            fallbackMethod = "companyBreakerFallback")
    public List<JobDTO> findAll() {
//        System.out.println("Attempt: "+ ++attempt);
        return jobRepository.findAll().stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> companyBreakerFallback(Exception e) {
        List<String> list = new ArrayList<>();
        list.add("Dummy");
        return list;
    }

    private JobDTO convertToDTO(Job job) {
//        Company company = restTemplate.getForObject(
//                "http://COMPANY-SERVICE:8081/companies/"+ job.getCompanyId(),
//                Company.class);
//
//        ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange(
//                "http://REVIEW-SERVICE:8083/reviews?companyId="+ job.getCompanyId(),
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<Review>>(){
//                });
//
//        List<Review> reviews = reviewResponse.getBody();

        Company company = companyClient.getCompany(job.getCompanyId());
        List<Review> reviews = reviewClient.getReviews(job.getCompanyId());
        return JobMapper.mapToJobWithCompanyDTO(job, company, reviews);
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        return convertToDTO(jobRepository.findById(id).orElse(null));
    }

    @Override
    public Boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if(jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setLocation(updatedJob.getLocation());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setCompanyId(updatedJob.getCompanyId());
            jobRepository.save(job);
            return true;
        } else
            return false;
    }
}
