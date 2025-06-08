package org.example;

import java.util.*;

public class Project {
    private final String name;
    private final Set<Employee> employees;
    private final List<Task> tasks;

    public Project(String name) {
        this.name = name;
        this.employees = new HashSet<>();
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
