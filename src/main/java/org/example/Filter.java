package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class Filter {

    public ArrayList<Task> filterTasksByData(ArrayList<Task> tasks, LocalDate fromDate, LocalDate toDate) {
        HashSet<Task> filteredTasks = new HashSet<>();
        for (Task task : tasks) {
            if (task.getDate().isAfter(fromDate) && task.getDate().isBefore(toDate)) {
                filteredTasks.add(task);
            }
        }
        return tasks;
    }

    public HashSet<Project> filterProjectsByData(HashSet<Project> projects, LocalDate fromDate, LocalDate toDate) {
        HashSet<Project> filteredProjects = new HashSet<>();
        for (Project project : projects) {
            Project newProject = new Project(project.getName());
            for (Task task : project.getTasks()) {
                if (task.getDate().isAfter(fromDate) && task.getDate().isBefore(toDate)) {
                    newProject.addTask(task);
                }
            }
            for (Employee employee : project.getEmployees()) {
                Employee newEmployee = new Employee(employee.getName());
                for (Task task : employee.getTasks()) {
                    if (task.getDate().isAfter(fromDate) && task.getDate().isBefore(toDate)) {
                        newEmployee.addTask(task);
                    }
                }
                newProject.addEmployee(newEmployee);
            }
            filteredProjects.add(newProject);
        }
        return filteredProjects;
    }

    public HashSet<Employee> filterEmployeesByData(HashSet<Employee> projects, LocalDate fromDate, LocalDate toDate) {
        HashSet<Employee> filteredProjects = new HashSet<>();
        for (Employee employee : projects) {
            Employee newEmployee = new Employee(employee.getName());
            for (Task task : employee.getTasks()) {
                if (task.getDate().isAfter(fromDate) && task.getDate().isBefore(toDate)) {
                    newEmployee.addTask(task);
                }
            }
            for (Project project : employee.getProjects()) {
                Project newProject = new Project(project.getName());
                for (Task task : project.getTasks()) {
                    if (task.getDate().isAfter(fromDate) && task.getDate().isBefore(toDate)) {
                        newProject.addTask(task);
                    }
                }
                newEmployee.addProject(newProject);
            }
            filteredProjects.add(newEmployee);
        }
        return filteredProjects;
    }
}
