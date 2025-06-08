package org.example;
import java.util.*;
import java.util.stream.Collectors;

public class ReportEmployee implements IReport {

    Set<Employee> employees;

    public ReportEmployee(Set<Employee> employees) {
        this.employees = employees;
    }

    public float getFloatSumOfHours(Employee employee) {
        float sumOfHours = 0;
        for (Task task : employee.getTasks()) {
            sumOfHours += task.getDuration();
        }
        return sumOfHours;
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
        HashMap<String, Float> set = new HashMap<>();
        for (Employee employee : employees) {
            set.put(employee.getName(), getFloatSumOfHours(employee));
        }
        List<Map.Entry<String, Float>> sorted = set.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());

        for (Map.Entry<String, Float> entry : sorted) {
            sb.append(entry.getKey());
            sb.append("\t");
            sb.append(entry.getValue());
            sb.append("\n");
        }

        return sb.toString();
    }
}
