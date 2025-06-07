package org.example;

import java.util.ArrayList;
import java.util.HashSet;

public class ReportProject implements IReport {

    HashSet<Project> projects;

    public ReportProject(HashSet<Project> projects) {
        this.projects = projects;
    }

    public String getStringSumOfHours(Project project) {
        float sumOfHours = 0;
        for (Task task : project.getTasks()) {
            sumOfHours += task.getDuration();
        }
        return String.format("%.2f", sumOfHours);
    }

    public String getName() {
        return "Employee Report";
    }

    public String getReportString() {
        StringBuilder sb = new StringBuilder();
        for (Project project : projects) {
            sb.append(project.getName()).append("\t");
            sb.append(getStringSumOfHours(project)).append("\n");
        }
        return sb.toString();
    }
}
