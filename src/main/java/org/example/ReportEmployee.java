package org.example;

import java.util.HashSet;

public class ReportEmployee implements IReport {

    HashSet<Employee> employees;

    public ReportEmployee(HashSet<Employee> employees) {
        this.employees = employees;
    }

    public String getStringSumOfHours(Employee employee) {
        float sumOfHours = 0;
        for (Task task : employee.getTasks()) {
            sumOfHours += task.getDuration();
        }
        return String.format("%.2f", sumOfHours);
    }

    public String getName() {
        return "Employee Report";
    }

    public String getReportString() {
        StringBuilder sb = new StringBuilder();
        for (Employee employee : employees) {
            sb.append(employee.getName()).append("\t");
            sb.append(getStringSumOfHours(employee)).append("\n");
        }
        return sb.toString();
    }
}
