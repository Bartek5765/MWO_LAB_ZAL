package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;


import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FullTest {
    Employee e1;
    Employee e2;

    Project p1;
    Project p2;

    Task task1;
    Task task2;

    List<Task> tasks;
    Set<Employee> employees;
    Set<Project> projects;

    @BeforeEach
    void init() {
        e1 = new Employee("Jan");
        p1 = new Project("Project 1");
        task1 = new Task(e1, p1, LocalDate.of(2020, 1, 1), "meeting", 100);
        e1.addTask(task1);
        e1.addProject(p1);
        p1.addTask(task1);
        p1.addEmployee(e1);

        e2 = new Employee("John");
        p2 = new Project("Project 2");
        task2 = new Task(e2, p2, LocalDate.of(2021, 3, 2), "coding", 50);
        e2.addTask(task2);
        e2.addProject(p2);
        p2.addTask(task2);
        p2.addEmployee(e2);

        employees = new HashSet<>();
        employees.add(e1);
        employees.add(e2);

        projects = new HashSet<>();
        projects.add(p1);
        projects.add(p2);

        tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
    }

    @Test
    void testEmployeeTasks() {
        assertEquals("meeting", e1.getTasks().getFirst().getName());
        assertEquals("coding", e2.getTasks().getFirst().getName());
    }

    @Test
    void testProjectTasks() {
        assertEquals("meeting", p1.getTasks().getFirst().getName());
        assertEquals("coding", p2.getTasks().getFirst().getName());
    }

    @Test
    void testEmployeeHasProject() {
        assertTrue(e1.getProjects().contains(p1));
        assertTrue(e2.getProjects().contains(p2));
    }

    @Test
    void testProjectHasEmployee() {
        assertTrue(p1.getEmployees().contains(e1));
        assertTrue(p2.getEmployees().contains(e2));
    }

    @Test
    void taskHasCorrectOutputs() {
        assertEquals(e1, task1.getEmployee());
        assertEquals(p1, task1.getProject());
        assertEquals(LocalDate.of(2021, 3, 2), task2.getDate());
        assertEquals(50, task2.getDuration());
    }

    @Test
    void testTotalTimePerEmployeeCanBeCounted() {
        e1.addTask(task2);
        float sum = 0;

        for (Task task : e1.getTasks()) {
            sum += task.getDuration();
        }

        assertEquals(150, sum);
    }

    @Test
    void testTotalTimePerProjectCanBeCounted() {
        p1.addTask(task2);
        float sum = 0;

        for (Task task : p1.getTasks()) {
            sum += task.getDuration();
        }

        assertEquals(150, sum);
    }

    @Test
    void testEmployeeNameIsUniqueKey() {
        assertTrue(employees.contains(new Employee("Jan")));
        assertTrue(employees.contains(new Employee("John")));
    }

    @Test
    void testProjectNameIsUniqueKey() {
        assertTrue(projects.contains(new Project("Project 1")));
        assertTrue(projects.contains(new Project("Project 2")));
    }

    @Test
    void testPrintingProjectReportMockUp() {
        p1.addTask(task2);
        p2.addTask(task2);

        for (Project project : projects) {
            float sum = 0;
            for (Task task : project.getTasks()) {
                sum += task.getDuration();
            }
            System.out.println(project.getName() + " has " + sum + " hours in total");
        }
    }

    @Test
    void testPrintingEmployeeReportMockUp() {
        e1.addTask(task2);
        e2.addTask(task2);

        for (Employee employee : employees) {
            float sum = 0;
            for (Task task : employee.getTasks()) {
                sum += task.getDuration();
            }
            System.out.println(employee.getName() + " has " + sum + " hours in total");
        }
    }

    @Test
    void testMultipleTasksAlwaysTreatedAsDifferentAndHaveCorrectTimeOutput() {
        // initial 150 + 400 = 550
        tasks.add(task2);
        tasks.add(task2);
        tasks.add(task2);
        tasks.add(task2);
        tasks.add(task2);
        tasks.add(task2);
        tasks.add(task2);
        tasks.add(task2);
        float sum = 0;
        for (Task task : tasks) {
            sum += task.getDuration();
        }
        assertEquals(550, sum);
    }


}
