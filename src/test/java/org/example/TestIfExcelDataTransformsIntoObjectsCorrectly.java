package org.example;

import org.example.loader.ExcelLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestIfExcelDataTransformsIntoObjectsCorrectly {
    public ArrayList<Task> tasks;
    public Set<Employee> employees;
    TerminalInput input;
    public Set<Project> projects;

    @BeforeEach
    public void beforeAll() throws ExcelLoader.ExcelLoadException, IOException {
        this.tasks = new ArrayList<>();
        this.employees = new HashSet<>();
        this.projects = new HashSet<>();

        String simulatePath = "employees\n2000-01-01\n/var/home/student/Downloads\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatePath.getBytes());
        System.setIn(in);

        TerminalInput input = new TerminalInput();
        input = input.readFromConsole();
        ExcelLoader excelLoader = new ExcelLoader();
        excelLoader.setDirectoryPath(input.getRootPath());
        ExcelLoader.LoadResult result = excelLoader.loadAllData();
        this.employees.addAll(result.getEmployees());
        this.projects.addAll(result.getProjects());
        this.tasks.addAll(result.getTasks());
    }

    @Test
    public void TestIfExcelDataTransformsIntoObjectsCorrectly() throws ExcelLoader.ExcelLoadException, IOException {
        for (Employee employee : employees) {
            System.out.println("Name: " + employee.getName());
            for (Project project : employee.getProjects()) {
                System.out.println("    Project: " + project.getName());
                for (Task task : project.getTasks()) {
                    System.out.println("        Task: " + task.getName() + " done on " + task.getDate() + " during " + task.getDuration());
                }
            }
        }
    }
}
