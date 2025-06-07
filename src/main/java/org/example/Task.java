package org.example;

import java.time.LocalDate;

public class Task {
    private final Employee employee;
    private final Project project;
    private final LocalDate date;
    private final String name;
    private final float duration;

    public Task(Employee employee, Project project, LocalDate date, String name, float duration) {
        this.employee = employee;
        this.project = project;
        this.date = date;
        this.name = name;
        this.duration = duration;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Project getProject() {
        return project;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public float getDuration() {
        return duration;
    }
}
