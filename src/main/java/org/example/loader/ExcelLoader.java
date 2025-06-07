package org.example.loader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.example.Employee;
import org.example.Project;
import org.example.Task;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class ExcelLoader {

    private String path;
    private final Map<String, Employee> employeeCache = new HashMap<>();
    private final Map<String, Project> projectCache = new HashMap<>();

    public ExcelLoader() {}

    public ExcelLoader(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        employeeCache.clear();
        projectCache.clear();
    }


    public LoadResult loadData() throws IOException, ExcelLoadException {
        if (path == null || path.trim().isEmpty()) {
            throw new ExcelLoadException("Ścieżka do pliku nie może być pusta");
        }
    System.out.println("Loading data from " + path);
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new HSSFWorkbook(fis)) {
            System.out.println("WOrkbook created successfully"+ workbook);
            Set<Employee> employees = new HashSet<>();
            Set<Project> projects = new HashSet<>();
            ArrayList<Task> tasks = new ArrayList<>();

            Employee employee = new Employee("Jan Kowalski");

            for (Sheet sheet : workbook) {
                Project project = new Project(sheet.getSheetName());
                System.out.println("Sheet created successfully"+ sheet);
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new ExcelLoadException("Plik Excel jest pusty lub nie zawiera nagłówków");
                }


                // Przetwarzaj wiersze danych (od wiersza 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isEmptyRow(row)) {
                        continue;
                    }


                    System.out.println("Processing row " + (i + 1) + ": " + row);
                    try {
                        Task taskData = parseRow(row, i + 1);
                        System.out.println(taskData);

                        Task task = new Task(employee, project, taskData.getDate(), taskData.taskName, taskData.getDuration());

                        employee.addTask(task);
                        employee.addProject(project);
                        project.addTask(task);
                        project.addEmployee(employee);

                        employees.add(employee);
                        projects.add(project);
                        tasks.add(task);

                    } catch (Exception e) {
                        throw new ExcelLoadException(String.format("Błąd w wierszu %d: %s", i + 1, e.getMessage()));
                    }
            }

            }

            return new LoadResult(employees, projects , tasks);

        } catch (IOException e) {
            throw new IOException("Nie można odczytać pliku: " + path, e);
        }
    }

    private void validateHeaders(Row headerRow) throws ExcelLoadException {
        String[] expectedHeaders = {"data", "zadanie", "Czas [h]"};

        if (headerRow.getLastCellNum() < expectedHeaders.length) {
            throw new ExcelLoadException(
                    String.format("Oczekiwano %d kolumn, znaleziono %d",
                            expectedHeaders.length, headerRow.getLastCellNum()));
        }

        for (int i = 0; i < expectedHeaders.length; i++) {
            Cell cell = headerRow.getCell(i);
            String actualHeader = getCellValueAsString(cell).trim();

            if (!expectedHeaders[i].equalsIgnoreCase(actualHeader)) {
                throw new ExcelLoadException(
                        String.format("Nieprawidłowy nagłówek w kolumnie %d. Oczekiwano '%s', znaleziono '%s'",
                                i + 1, expectedHeaders[i], actualHeader));
            }
        }
    }

    private Task parseRow(Row row, int rowNumber) throws ExcelLoadException {
        try {
            LocalDate date = getCellValueAsDate(row.getCell(0));

            String taskName = getCellValueAsString(row.getCell(1)).trim();
            Float duration = getCellValueAsFloat(row.getCell(2));

            // Walidacja

            if (taskName.isEmpty()) {
                throw new ExcelLoadException("Nazwa zadania nie może być pusta");
            }
            if (date == null) {
                throw new ExcelLoadException("Data nie może być pusta");
            }
            if (duration == null || duration <= 0) {
                throw new ExcelLoadException("Czas trwania musi być liczbą większą od 0");
            }

            System.out.println(  taskName + " " + date + " " + duration);

            return new Task(
                    date,
                    taskName,
                    duration
            );
//            return new Task(employeeName, projectName, taskName, date, duration);

        } catch (Exception e) {
            throw new ExcelLoadException("Błąd parsowania wiersza: " + e.getMessage());
        }
    }

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < 5; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        return null;
    }

    private Float getCellValueAsFloat(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> (float) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Float.parseFloat(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    public static class LoadResult {
        private final Set<Employee> employees;
        private final Set<Project> projects;
        private final ArrayList<Task> tasks;

        public LoadResult(Set<Employee> employees, Set<Project> projects, ArrayList<Task> tasks) {
            this.employees = employees;
            this.projects = projects;
            this.tasks = tasks;
        }

        public Set<Employee> getEmployees() { return employees; }
        public Set<Project> getProjects() { return projects; }
        public ArrayList<Task> getTasks() {
            return new ArrayList<>(tasks);
        }
    }

    public static class ExcelLoadException extends Exception {
        public ExcelLoadException(String message) {
            super(message);
        }

        public ExcelLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
