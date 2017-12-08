package com.talend.tuj.generator.utils;

import com.talend.tuj.generator.utils.Job;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.List;

public class TUJ {
    private Document project;
    private Job starter;
    private String name;
    private String projectName;
    private List<Path> resources;

    public TUJ(Job job, Document project, List<Path> resources, String name, String projectName){
        this.starter = job;
        this.project = project;
        this.resources = resources;
        this.name = name;
        this.projectName = projectName;
        job.setTuj(this);
    }

    public Job getStarterJob() {
        return starter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Path> getResources() {
        return resources;
    }
}
