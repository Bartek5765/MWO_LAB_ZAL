package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Employee {
    public final ArrayList<Task> tasks;
    public final Set<Project> projects;
    public final String name;

    public Employee(String name) {
        this.tasks = new ArrayList<>();
        this.projects = new HashSet<>();
        this.name = name;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public Set<Project> getProjects() {
        return this.projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
