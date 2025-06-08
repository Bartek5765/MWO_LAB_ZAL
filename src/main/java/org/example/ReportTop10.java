package org.example;

import java.util.HashSet;
import java.util.Set;

public class ReportTop10 extends ReportEmployee implements IReport {

    String name;
    Set<Employee> top10Employees;

    public ReportTop10(Set<Employee> employees, int topNumber) {
        super(employees);
        findTop10(this.employees, topNumber);
    }

    public Employee findEmployeeWithLowestHours(Set<Employee> employees) {
        float lowestHours = -1;
        Employee foundedEmployee = null;
        for (Employee employee : employees) {
            if (getFloatSumOfHours(employee) < lowestHours || lowestHours == -1) {
                foundedEmployee = employee;
                lowestHours = getFloatSumOfHours(employee);
            }
        }
        return foundedEmployee;
    }

    public float getFloatSumOfHours(Employee employee) {
        float sumOfHours = 0;
        for (Task task : employee.getTasks()) {
            sumOfHours += task.getDuration();
        }
        return sumOfHours;
    }

    public void findTop10(Set<Employee> employees, int topNumber) {
        this.top10Employees = new HashSet<>();
        for (Employee employee : employees) {
            if (top10Employees.size() < topNumber) {
                top10Employees.add(employee);
            } else {
                Employee lowestTop = findEmployeeWithLowestHours(this.top10Employees);
                if (getFloatSumOfHours(lowestTop) < getFloatSumOfHours(employee)) {
                    top10Employees.remove(lowestTop);
                    top10Employees.add(employee);
                }
            }
        }
        this.name = "Top " + topNumber + " employees";
    }

    @Override
    public String getName() {
        return this.name;
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
