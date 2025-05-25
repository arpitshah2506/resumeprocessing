package com.example.demo.service;

import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import com.example.demo.model.ResumeData;

@Service
public class ResumeParserService {

    public ResumeData parse(InputStream resumeStream) throws Exception {
        Tika tika = new Tika();
        String text = tika.parseToString(resumeStream);

        // dummy logic
        String name = extractName(text.trim());

        return new ResumeData(name);
    }

    private String extractName(String text) {
        return text.lines().findFirst().orElse("Unknown");
    }
}