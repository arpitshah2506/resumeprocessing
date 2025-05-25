package com.example.demo.model;

import lombok.Data;

@Data
public class ResumeData {
    private String name;
    // private List<String> skills;
    
    public ResumeData(String name) {
        this.name = name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
}
