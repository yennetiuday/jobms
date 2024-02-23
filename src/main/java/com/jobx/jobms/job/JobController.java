package com.jobx.jobms.job;

import com.jobx.jobms.job.dto.JobDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("jobs")
public class JobController {
    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> findAll() {
        return ResponseEntity.ok(jobService.findAll());
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job) {
        jobService.createJob(job);
        return  new ResponseEntity<>("Job added Successfully", HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        JobDTO jobDTO = jobService.getJobById(id);
        if(Objects.nonNull(jobDTO))
            return ResponseEntity.ok(jobDTO);
        else
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable Long id) {
        boolean deleted = jobService.deleteJobById(id);
        if(deleted)
            return ResponseEntity.ok("Job deleted successfully");
        else
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateJob(@RequestBody Job updatedJob, @PathVariable Long id) {
        boolean updated = jobService.updateJob(id, updatedJob);
        if(updated)
            return new ResponseEntity<>("Job updated successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
    }
}
