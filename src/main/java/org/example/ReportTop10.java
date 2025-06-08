package org.example;

import java.util.HashSet;
import java.util.Set;

public class ReportTop10 extends ReportEmployee implements IReport {

    Set<Employee> employees;
    Set<Employee> top10Employees;

    public ReportTop10(Set<Employee> employees) {
        super(employees);
    }

    public Set<Employee> findTop10(Set<Employee> employees) {
        this.top10Employees = new HashSet<>(employees);
        for (Employee employee : employees) {
            if (top10Employees.size() < 10) {
                top10Employees.add(employee);
            }
        }
        return top10Employees;
    }

    @Override
    public String getName() {
        return "Top 10 Employees";
    }

    @Override
    public String getReportString() {
        StringBuilder sb = new StringBuilder();
        for (Employee employee : top10Employees) {
            sb.append(employee.getName()).append("\t");
            sb.append(getStringSumOfHours(employee)).append("\n");
        }
        return sb.toString();
    }
}
