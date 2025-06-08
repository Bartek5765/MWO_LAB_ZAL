package org.example;

import org.example.Employee;
import org.example.Project;
import org.example.Task;
import org.example.TerminalInput;
import org.example.loader.ExcelLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIfExcelDataTransformsIntoObjectsCorrectly {
    public ArrayList<Task> tasks;
    public Set<Employee> employees;
    TerminalInput input;
    public Set<Project> projects;

    @Test
    public void TestIfExcelDataTransformsIntoObjectsCorrectly() throws ExcelLoader.ExcelLoadException, IOException {
        this.tasks = new ArrayList<>();
        this.employees = new HashSet<>();
        this.projects = new HashSet<>();

        TerminalInput input = new TerminalInput();
        String[] args = {"employees", "/var/home/student/IdeaProjects/MWO_LAB_ZAL/sample-data/Projekt2/2023/02", "2000-01"};
        input.fromArgs(args);
        ExcelLoader excelLoader = new ExcelLoader();
        excelLoader.setDirectoryPath(input.getRootPath());
        ExcelLoader.LoadResult result = excelLoader.loadAllData();
        this.employees.addAll(result.getEmployees());
        this.projects.addAll(result.getProjects());
        this.tasks.addAll(result.getTasks());

        for (Employee employee : employees) {
            System.out.println("Name: " + employee.getName());
            for (Project project : employee.getProjects()) {
                System.out.println("    Project: " + project.getName());
                for (Task task : project.getTasks()) {
                    System.out.println("        Task: " + task.getName() + " done on " + task.getDate() + " during " + task.getDuration());
                }
            }
        }

        assertEquals(2, this.projects.size());
        assertEquals(1, this.employees.size());
        assertEquals(9, this.tasks.size());
    }

    @Test
    public void TestIfExcelDataFilteredByDataCorrectly() throws ExcelLoader.ExcelLoadException, IOException {
        this.tasks = new ArrayList<>();
        this.employees = new HashSet<>();
        this.projects = new HashSet<>();

        TerminalInput input = new TerminalInput();
        String[] args = {"employees", "/var/home/student/IdeaProjects/MWO_LAB_ZAL/sample-data/Projekt2/2023/02", "2023-02"};
        input.fromArgs(args);
        ExcelLoader excelLoader = new ExcelLoader();
        excelLoader.setDirectoryPath(input.getRootPath());
        ExcelLoader.LoadResult result = excelLoader.loadAllData();
        this.employees.addAll(result.getEmployees());
        this.projects.addAll(result.getProjects());
        this.tasks.addAll(result.getTasks());
        Filter filter = new Filter();
        tasks = filter.filterTasksByData(tasks, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));


        assertEquals(2, this.projects.size());
        assertEquals(1, this.employees.size());
        assertEquals(3, this.tasks.size());
    }


}
