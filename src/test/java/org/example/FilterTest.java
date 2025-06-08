package org.example;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilterTest {

    @Test
    void testFilterTasksByDate() {
        Task t1 = new Task(LocalDate.of(2022, 1, 1), "Task1", 1);
        Task t2 = new Task(LocalDate.of(2022, 2, 1), "Task2", 2);
        Task t3 = new Task(LocalDate.of(2022, 3, 1), "Task3", 3);
        ArrayList<Task> tasks = new ArrayList<>(List.of(t1, t2, t3));

        Filter filter = new Filter();
        LocalDate from = LocalDate.of(2022, 1, 15);
        LocalDate to = LocalDate.of(2022, 2, 15);

        ArrayList<Task> result = filter.filterTasksByData(tasks, from, to);

        assertEquals(1, result.size());
        assertEquals("Task2", result.get(0).getName());
    }

    @Test
    void testFilterProjectsByDate() {
        Task t1 = new Task(LocalDate.of(2022, 1, 10), "Task1", 1);
        Task t2 = new Task(LocalDate.of(2022, 2, 10), "Task2", 2);
        Task t3 = new Task(LocalDate.of(2022, 3, 10), "Task3", 3);

        Project project = new Project("ProjectX");
        project.addTask(t1);
        project.addTask(t2);
        project.addTask(t3);

        Employee emp = new Employee("Jan");
        emp.addTask(t2);
        emp.addTask(t3);
        project.addEmployee(emp);

        HashSet<Project> inputProjects = new HashSet<>();
        inputProjects.add(project);

        Filter filter = new Filter();
        LocalDate from = LocalDate.of(2022, 2, 1);
        LocalDate to = LocalDate.of(2022, 3, 1);

        HashSet<Project> filtered = filter.filterProjectsByData(inputProjects, from, to);
        assertEquals(1, filtered.size());

        Project filteredProject = filtered.iterator().next();
        assertEquals(1, filteredProject.getTasks().size());
        assertEquals("Task2", filteredProject.getTasks().iterator().next().getName());

        Employee filteredEmployee = filteredProject.getEmployees().iterator().next();
        assertEquals(1, filteredEmployee.getTasks().size());
        assertEquals("Task2", filteredEmployee.getTasks().get(0).getName());
    }

    @Test
    void testFilterEmployeesByDate() {
        Task t1 = new Task(LocalDate.of(2022, 1, 10), "Task1", 1);
        Task t2 = new Task(LocalDate.of(2022, 2, 10), "Task2", 2);

        Project project = new Project("Alpha");
        project.addTask(t1);
        project.addTask(t2);

        Employee emp = new Employee("Anna");
        emp.addTask(t1);
        emp.addTask(t2);
        emp.addProject(project);

        HashSet<Employee> employees = new HashSet<>();
        employees.add(emp);

        Filter filter = new Filter();
        LocalDate from = LocalDate.of(2022, 2, 1);
        LocalDate to = LocalDate.of(2022, 2, 28);

        HashSet<Employee> filtered = filter.filterEmployeesByData(employees, from, to);
        assertEquals(1, filtered.size());

        Employee filteredEmp = filtered.iterator().next();
        assertEquals(1, filteredEmp.getTasks().size());
        assertEquals("Task2", filteredEmp.getTasks().get(0).getName());

        Project filteredProj = filteredEmp.getProjects().iterator().next();
        assertEquals(1, filteredProj.getTasks().size());
        assertEquals("Task2", filteredProj.getTasks().iterator().next().getName());
    }
}
