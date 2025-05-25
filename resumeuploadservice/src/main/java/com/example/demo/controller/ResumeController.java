package com.example.demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Resume;
import com.example.demo.repository.ResumeRepository;
import com.example.demo.service.S3Service;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired private S3Service s3Service;
    @Autowired private ResumeRepository repo;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
        @RequestParam MultipartFile file,
        @RequestParam String name,
        @RequestParam String job
    ) throws IOException {

        String fileUrl = s3Service.upload(file);

        Resume resume = new Resume();
        resume.setFileUrl(fileUrl);
        resume.setApplicantName(name);
        resume.setJobTitle(job);
        repo.save(resume);

        return ResponseEntity.ok("Uploaded to: " + fileUrl);
    }
}
