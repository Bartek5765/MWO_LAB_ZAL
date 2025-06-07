package org.example;
import java.util.Set;

public class ReportProject implements IReport {

    Set<Project> projects;

    public ReportProject(Set<Project> projects) {
        this.projects = projects;
    }

    public String getStringSumOfHours(Project project) {
        float sumOfHours = 0;
        for (Task task : project.getTasks()) {
            sumOfHours += task.getDuration();
        }
        return String.format("%.2f", sumOfHours);
    }

    public String getSumOfEmployeeHours(Project project, Employee employee) {
        float sumOfHours = 0;
        for (Task task : employee.getTasks()) {
            if (task.getProject().getName().equals(project.getName())) {
                sumOfHours += task.getDuration();
            }
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
            sb.append(">>>>\n");
            sb.append("Project Employees: \n");
            for (Employee employee : project.getEmployees()) {
                sb.append("\t").append(employee.getName()).append("\t");
                sb.append(getSumOfEmployeeHours(project, employee)).append("\n");
            }
            sb.append("<<<<\n");
        }
        return sb.toString();
    }
}
