package org.example;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testConstructorInitializesFields() {
        Employee employee = new Employee("Jan");

        assertEquals("Jan", employee.getName());
        assertTrue(employee.getTasks().isEmpty());
        assertTrue(employee.getProjects().isEmpty());
    }

    @Test
    void testAddTask() {
        Employee employee = new Employee("Anna");
        Task task = new Task(LocalDate.of(2022, 1, 1), "Task1", 2);

        employee.addTask(task);

        List<Task> tasks = employee.getTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
    }

    @Test
    void testAddProject() {
        Employee employee = new Employee("Tomek");
        Project project = new Project("Project A");

        employee.addProject(project);

        Set<Project> projects = employee.getProjects();
        assertEquals(1, projects.size());
        assertTrue(projects.contains(project));
    }

    @Test
    void testEqualsAndHashCode() {
        Employee e1 = new Employee("Kasia");
        Employee e2 = new Employee("Kasia");
        Employee e3 = new Employee("Adam");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, e3);
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }

    @Test
    void testEqualsWithSameInstance() {
        Employee employee = new Employee("Zofia");
        assertEquals(employee, employee);
    }

    @Test
    void testEqualsWithNullAndDifferentType() {
        Employee employee = new Employee("Ewa");
        assertNotEquals(employee, null);
        assertNotEquals(employee, "Ewa");
    }
}
