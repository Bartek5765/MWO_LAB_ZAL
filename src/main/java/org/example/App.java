package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class App {
    public final ArrayList<Task> tasks;
    public final Set<Employee> employees;
    TerminalInput input;
    public final Set<Project> projects;
//    public final ReportType reportType;

    public App() {
        tasks = new ArrayList<>();
        employees = new HashSet<>();
        projects = new HashSet<>();
    }

    public void run() {
        TerminalInput input = new TerminalInput();
        input.readFromConsole();
    }
}
