package org.example;

import org.example.loader.ExcelLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class App {
    public ArrayList<Task> tasks;
    public Set<Employee> employees;
    TerminalInput input;
    public Set<Project> projects;
//    public final ReportType reportType;
private final ExcelLoader excelLoader;


    public App() {
        tasks = new ArrayList<>();
        employees = new HashSet<>();
        projects = new HashSet<>();
        this.excelLoader = new ExcelLoader();
    }

    public void run() throws Exception {
        TerminalInput input = new TerminalInput();
        this.input = input.readFromConsole();
        loadDataFromExcel();
    }

    private void loadDataFromExcel() throws Exception {

        excelLoader.setPath(input.getRootPath());
        ExcelLoader.LoadResult result = excelLoader.loadData();

        this.employees = result.getEmployees();
        this.projects = result.getProjects();
        this.tasks = result.getTasks();

        System.out.println("Employees: " + employees.size());
        System.out.println("Projects: " + projects.size());
        System.out.println("Tasks: " + tasks.size());


    }

}
