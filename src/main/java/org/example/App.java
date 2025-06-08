package org.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
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
        CliOptions cliOptions = new CliOptions();
        CommandLine lines = cliOptions.parse(args);
        this.input.fromArgs(lines);
        run();
    }

    public void run() throws Exception {

        excelLoader.setDirectoryPath(this.input.getRootPath());

        ExcelLoader.LoadResult result = excelLoader.loadAllData();
        this.employees = result.getEmployees();
        this.projects = result.getProjects();
        this.tasks = result.getTasks();

        Filter filter = new Filter();
        this.employees = filter.filterEmployeesByData((HashSet<Employee>) this.employees, this.input.getFromDate(), this.input.getToDate());
        this.projects = filter.filterProjectsByData((HashSet<Project>) this.projects, this.input.getFromDate(), this.input.getToDate());
        this.tasks = filter.filterTasksByData( this.tasks, this.input.getFromDate(), this.input.getToDate());

        switch (this.input.getReportType()) {
            case employees:
                report = new ReportEmployee(this.employees);
                break;
            case projects:
                report = new ReportProject(this.projects);
                break;
            case top:
                report = new ReportTop10(this.employees, this.input.getTopNumber());
            default:
                break;
        }
        printer = new ReportPrinter();
        String s = printer.printToTerminal(report);
    }
}
