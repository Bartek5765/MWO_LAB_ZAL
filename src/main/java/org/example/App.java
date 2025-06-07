package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class App {
    public final ArrayList<Task> tasks;
    public final Set<Employee> employees;
//    TerminalInput input;
    public final Set<Project> projects;
    public final ReportType reportType;

    public App(ReportType reportType) {
        tasks = new ArrayList<>();
        employees = new HashSet<>();
        projects = new HashSet<>();
        this.reportType = reportType;
    }
}
