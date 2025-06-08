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

    public App(String[] args) throws Exception {
        tasks = new ArrayList<>();
        employees = new HashSet<>();
        projects = new HashSet<>();
        this.excelLoader = new ExcelLoader();
        this.input = new TerminalInput();
        this.input.fromArgs(args);
        run();
    }

    public void run() throws Exception {

        excelLoader.setDirectoryPath(this.input.getRootPath());

        ExcelLoader.LoadResult result = excelLoader.loadAllData();
        this.employees = result.getEmployees();
        this.projects = result.getProjects();
        this.tasks = result.getTasks();

        switch (this.input.getReportType()) {
            case employees:
                report = new ReportEmployee(this.employees);
                break;
            case projects:
                report = new ReportProject(this.projects);
                break;
            case top10:
                report = new ReportTop10(this.employees, this.input.getTopNumber());
            default:
                break;
        }
        printer = new ReportPrinter();
        String s = printer.printToTerminal(report);
    }
}
