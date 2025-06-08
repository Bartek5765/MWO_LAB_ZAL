package org.example;

import java.time.LocalDate;

public class Task {
    public String projectName;
    public String taskName;
    private Employee employee;
    private  Project project;
    private  LocalDate date;
    private  String name;
    private  float duration;
    public String employeeName;

    public Task(Employee employee, Project project, LocalDate date, String name, float duration) {
        this.employee = employee;
        this.project = project;
        this.date = date;
        this.name = name;
        this.duration = duration;
    }

    public Task(LocalDate date, String taskName, float duration) {
        this.date = date;
        this.taskName = taskName;
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
