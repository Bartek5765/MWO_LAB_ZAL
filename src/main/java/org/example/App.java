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
    IReport report;
    ReportPrinter printer;


    public App() throws Exception {
        tasks = new ArrayList<>();
        employees = new HashSet<>();
        projects = new HashSet<>();
        this.excelLoader = new ExcelLoader();
        run();
    }

    public void run() throws Exception {
        TerminalInput input = new TerminalInput();
        this.input = input.readFromConsole();
        excelLoader.setDirectoryPath(this.input.getRootPath());

        ExcelLoader.LoadResult result = excelLoader.loadAllData();
        this.employees.addAll(result.getEmployees());
        this.projects.addAll(result.getProjects());
        this.tasks.addAll(result.getTasks());

        switch (this.input.getReportType()) {
            case employees:
                report = new ReportEmployee(this.employees);
                break;
            case projects:
                report = new ReportProject(this.projects);
                break;
            default:
                break;
        }
        printer = new ReportPrinter();
        String s = printer.printToTerminal(report);
    }
}
