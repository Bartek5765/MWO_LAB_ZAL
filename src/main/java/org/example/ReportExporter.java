package org.example;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class ReportExporter {
    public static void generateAllReports(Set<Employee> employees, Set<Project> projects, ArrayList<Task> tasks) {
        try {
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            

           saveEmployeeReport(employees, "reports/pracownicy_" + timestamp + ".csv");
           saveProjectReport(projects, "reports/projekty_" + timestamp + ".csv");
            saveTaskReport(tasks, "reports/zadania_" + timestamp + ".csv");

        } catch (IOException e) {
            System.err.println("Błąd podczas generowania raportów: " + e.getMessage());
        }
    }

    public static void saveEmployeeReport(Set<Employee> employees, String fileName) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {"Pracownik", "Łączne godziny", "Liczba projektów", "Liczba zadań", "Lista projektów"};
            writer.writeNext(header);

            for (Employee employee : employees) {
                double employeeHours = employee.getTasks().stream()
                        .mapToDouble(Task::getDuration)
                        .sum();

                String projects = employee.getProjects().stream()
                        .map(Project::getName)
                        .collect(Collectors.joining("; "));

                String[] row = {
                        escapeCSV(employee.getName()),
                        String.format(Locale.US, "%.1f", employeeHours),
                        String.valueOf(employee.getProjects().size()),
                        String.valueOf(employee.getTasks().size()),
                        projects
                };
                writer.writeNext(row);
            }
        }
        System.out.println("Zapisano: " + fileName);
    }

public static void saveProjectReport(Set<Project> projects, String fileName) throws IOException {
    try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
        String[] header = {"Projekt", "Łączne godziny", "Liczba pracowników", "Liczba zadań", "Lista pracowników"};
        writer.writeNext(header);

        for (Project project : projects) {
            double projectHours = project.getTasks().stream()
                    .mapToDouble(Task::getDuration)
                    .sum();

            String employees = project.getEmployees().stream()
                    .map(Employee::getName)
                    .collect(Collectors.joining("; "));

            String[] row = {
                    escapeCSV(project.getName()),
                    String.format(Locale.US, "%.1f", projectHours),
                    String.valueOf(project.getEmployees().size()),
                    String.valueOf(project.getTasks().size()),
                    employees
            };
            writer.writeNext(row);
        }
    }
    System.out.println("Zapisano: " + fileName);
    }

    public static void saveTaskReport(ArrayList<Task> tasks, String fileName) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {"Data", "Pracownik", "Projekt", "Zadanie", "Godziny"};
            writer.writeNext(header);

            List<Task> sortedTasks = tasks.stream()
                    .sorted(Comparator.comparing(Task::getDate))
                    .collect(Collectors.toList());

            for (Task task : sortedTasks) {
                String[] row = {
                        task.getDate() != null ? task.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "",
                        escapeCSV(task.getEmployee() != null ? task.getEmployee().getName() : "Nieznany"),
                        escapeCSV(task.getProject() != null ? task.getProject().getName() : "Nieznany"),
                        escapeCSV(task.getName() != null ? task.getName() : "Bez nazwy"),
                        String.format(Locale.US, "%.1f", task.getDuration())
                };
                writer.writeNext(row);
            }
        }
        System.out.println("Zapisano: " + fileName);
    }


    private static String escapeCSV(String str) {
        if (str == null) return "";
        return str.replace("\n", " ")
                  .replace("\r", " ")
                  .replace("\"", "'")
                  .trim();
    }
}
