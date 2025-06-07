package org.example;

import java.util.ArrayList;
import java.util.HashSet;

public class ReportProject implements IReport {

    float sumOfHours;
    HashSet<Project> projects;

    public ReportProject(HashSet<Project> projects) {
        this.projects = projects;
    }

    public void sumHours() {
    }

    public String getReportString() {
        return null;
    }
}
