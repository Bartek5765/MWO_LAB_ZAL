package org.example;
import java.util.*;
import java.util.stream.Collectors;

public class ReportProject implements IReport {

    Set<Project> projects;

    public ReportProject(Set<Project> projects) {
        this.projects = projects;
    }

    public float getFloatSumOfHours(Project project) {
        float sumOfHours = 0;
        for (Task task : project.getTasks()) {
            sumOfHours += task.getDuration();
        }
        return sumOfHours;
    }

    public float getFloatSumOfHours(Project project, Employee employee) {
        float sumOfHours = 0;
        for (Task task : employee.getTasks()) {
            if (task.getProject().getName().equals(project.getName())) {
                sumOfHours += task.getDuration();
            }
        }
        return sumOfHours;
    }

    public String getStringSumOfHours(Project project) {
        return String.format("%.2f", getFloatSumOfHours(project));
    }

    public String getSumOfEmployeeHours(Project project, Employee employee) {
        return String.format("%.2f", getFloatSumOfHours(project, employee));
    }

    public String getName() {
        return "Employee Report";
    }

    public String getReportString() {
        StringBuilder sb = new StringBuilder();
        for (Project project : projects) {
            sb.append(project.getName()).append("\t");
            sb.append(getStringSumOfHours(project)).append("\n");
            sb.append(">>>>\n");
            sb.append("Project Employees: \n");

            HashMap<String, Float> set = new HashMap<>();
            for (Employee employee : project.getEmployees()) {
                set.put(employee.getName(), getFloatSumOfHours(project, employee));
            }
            List<Map.Entry<String, Float>> sorted = set.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());

            for (Map.Entry<String, Float> entry : sorted) {
                sb.append("\t");
                sb.append(entry.getKey());
                sb.append("\t");
                sb.append(entry.getValue());
                sb.append("\t");
                float procent = entry.getValue() / getFloatSumOfHours(project) * 100;
                sb.append(String.format("%.2f", procent)).append("%").append("\n");
            }
            sb.append("<<<<\n");
        }
        return sb.toString();
    }
}
