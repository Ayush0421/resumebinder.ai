package com.resumebinder.model;
import lombok.Data;
import java.util.List;

@Data
public class ResumeData {
    private String name;
    private String contactInfo;
    private List<Experience> experience;
    private List<Technology> technologies;
    private List<Project> projects;
    private List<String> achievements;
    private List<Education> education;
}
