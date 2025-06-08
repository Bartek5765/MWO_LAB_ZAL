package org.example.loader;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.example.Employee;
import org.example.Project;
import org.example.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

public class ExcelLoader {

    public String directoryPath;
    private boolean recursiveSearch = true;
    private final Map<String, Employee> employeeCache = new HashMap<>();
    private final Map<String, Project> projectCache = new HashMap<>();

    public ExcelLoader() {}

    public ExcelLoader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public ExcelLoader(String directoryPath, boolean recursiveSearch) {
        this.directoryPath = directoryPath;
        this.recursiveSearch = recursiveSearch;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
        employeeCache.clear();
        projectCache.clear();
    }

    public boolean isRecursiveSearch() {
        return recursiveSearch;
    }

    public void setRecursiveSearch(boolean recursiveSearch) {
        this.recursiveSearch = recursiveSearch;
    }


    public LoadResult loadAllData() throws IOException, ExcelLoadException {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new ExcelLoadException("Ścieżka do katalogu nie może być pusta");
        }

        List<String> excelFiles = recursiveSearch ?
                findExcelFilesRecursive(directoryPath) :
                findExcelFiles(directoryPath);

        if (excelFiles.isEmpty()) {
            throw new ExcelLoadException("Nie znaleziono plików Excel w katalogu: " + directoryPath);
        }


        Set<Employee> allEmployees = new HashSet<>();
        Set<Project> allProjects = new HashSet<>();
        List<Task> allTasks = new ArrayList<>();
        List<String> processingErrors = new ArrayList<>(); // Zbieranie błędów

        for (String filePath : excelFiles) {
            try {
                LoadResult result = loadDataFromFile(filePath);

                allEmployees.addAll(result.getEmployees());
                allProjects.addAll(result.getProjects());
                allTasks.addAll(result.getTasks());

            } catch (Exception e) {
                String errorMsg = "Błąd w pliku " + filePath + ": " + e.getMessage();
                processingErrors.add(errorMsg);
                System.err.println("⚠ " + errorMsg);
            }
        }

        if (allTasks.isEmpty() && !processingErrors.isEmpty()) {
            throw new ExcelLoadException("Nie udało się przetworzyć żadnego pliku. Błędy: " +
                    String.join("; ", processingErrors));
        }


        if (!processingErrors.isEmpty()) {
            System.out.println("⚠ Wystąpiły błędy w " + processingErrors.size() + " plikach");
        }

        return new LoadResult(allEmployees, allProjects, new ArrayList<>(allTasks), processingErrors);
    }


    private List<String> findExcelFiles(String directoryPath) throws IOException {
        List<String> excelFiles = new ArrayList<>();
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir)) {
            throw new IOException("Katalog nie istnieje: " + directoryPath);
        }

        if (!Files.isDirectory(dir)) {
            throw new IOException("Podana ścieżka nie jest katalogiem: " + directoryPath);
        }

        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> isExcelFile(path.toString()))
                    .map(Path::toString)
                    .forEach(excelFiles::add);
        }

        return excelFiles;
    }

    private List<String> findExcelFilesRecursive(String directoryPath) throws IOException {
        List<String> excelFiles = new ArrayList<>();
        Path dir = Paths.get(directoryPath);

        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> isExcelFile(path.toString()))
                    .map(Path::toString)
                    .forEach(excelFiles::add);
        }

        return excelFiles;
    }

    private boolean isExcelFile(String fileName) {
        String lowercaseName = fileName.toLowerCase();
        return lowercaseName.endsWith(".xls") || lowercaseName.endsWith(".xlsx");
    }

    public LoadResult loadDataFromFile(String filePath) throws IOException, ExcelLoadException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new HSSFWorkbook(fis)) {
            String fileName = filePath.substring(filePath.lastIndexOf('/')+1);

            FilePathInfo pathInfo = new FilePathInfo(filePath);

            if (!pathInfo.hasValidStructure()) {
                System.out.println("⚠ Nieprawidłowa struktura ścieżki: " + filePath);
            }

            Set<Employee> employees = new HashSet<>();
            Set<Project> projects = new HashSet<>();
            List<Task> tasks = new ArrayList<>();
            List<String> skippedRows = new ArrayList<>(); // Tracking pominiętych wierszy

            Employee employee = getOrCreateEmployee(pathInfo.getEmployeeName());

            for (Sheet sheet : workbook) {
                String projectName = sheet.getSheetName();

                Project project = getOrCreateProject(projectName);

                if (sheet.getLastRowNum() < 0) {
                    System.out.println("⚠ Plik " + fileName + " arkusz " + sheet.getSheetName() + " jest pusty - pomijam");
                    continue;
                }

                Row headerRow = sheet.getRow(0);
                if (headerRow == null || isEmptyRow(headerRow)) {
                    System.out.println("⚠ Plik " + fileName + " arkusz " + sheet.getSheetName() + " nie ma nagłówków - pomijam");
                    continue;
                }

                try {
                    validateHeaders(headerRow);
                } catch (ExcelLoadException e) {
                    System.out.println("⚠ Plik " + fileName +  " błędne nagłówki w arkuszu " + sheet.getSheetName() + ": " + e.getMessage());
                }

                int validTasksInSheet = 0;

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);

                    if (row == null || isEmptyRow(row)) {
                        continue;
                    }

                    try {
                        Task taskData = parseRowSafe(row, i + 1, sheet.getSheetName(), fileName);

                        if (taskData != null) {
                            Task task = new Task(employee, project, taskData.getDate(),
                                    taskData.getName(), taskData.getDuration());

                            employee.addTask(task);
                            employee.addProject(project);
                            project.addTask(task);
                            project.addEmployee(employee);

                            employees.add(employee);
                            projects.add(project);
                            tasks.add(task);
                            validTasksInSheet++;
                        }

                    } catch (Exception e) {
                        String errorMsg = String.format("Wiersz %d w arkuszu '%s': %s",
                                i + 1, sheet.getSheetName(), e.getMessage());
                        skippedRows.add(errorMsg);
                        System.out.println("⚠ Pomijam " + errorMsg);
                    }
                }
            }

            if (!skippedRows.isEmpty()) {
                System.out.println("📊 Pominięto " + skippedRows.size() + " problematycznych wierszy");
            }

            return new LoadResult(employees, projects, new ArrayList<>(tasks), skippedRows);

        } catch (IOException e) {
            throw new IOException("Nie można odczytać pliku: " + filePath, e);
        }
    }

    private Employee getOrCreateEmployee(String employeeName) {
        return employeeCache.computeIfAbsent(employeeName, Employee::new);
    }


    private Project getOrCreateProject(String projectName) {
        return projectCache.computeIfAbsent(projectName, Project::new);
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


    private Task parseRowSafe(Row row, int rowNumber, String sheetName, String fileName) {
        try {
            if (row.getLastCellNum() < 3) {
                return null;
            }

            LocalDate date = getCellValueAsDateSafe(row.getCell(0), fileName, sheetName, rowNumber);
            String taskName = getCellValueAsStringSafe(row.getCell(1), fileName, sheetName, rowNumber);
            Float duration = getCellValueAsFloatSafe(row.getCell(2), fileName, sheetName, rowNumber);

            if (taskName == null || taskName.trim().isEmpty()) {
                System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + ": pusta nazwa zadania - pomijam");
                return null;
            }

            if (date == null) {
                System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + ": nieprawidłowa data - pomijam");
                return null;
            }

            if (duration == null || duration <= 0) {
                System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + ": nieprawidłowy czas (" + duration + ") - pomijam");
                return null;
            }

            return new Task(date, taskName.trim(), duration);

        } catch (Exception e) {
            System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " błąd parsowania wiersza " + rowNumber + ": " + e.getMessage());
            return null;
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        int lastCellNum = row.getLastCellNum();
        if (lastCellNum <= 0) return true;

        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValueAsStringSafe(cell, "", "", 0);
            if (cellValue != null && !cellValue.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    private String getCellValueAsStringSafe(Cell cell, String fileName, String sheetName, int rowNumber) {
        if (cell == null) return "";

        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield cell.getDateCellValue().toString();
                    } else {
                        double numValue = cell.getNumericCellValue();
                        if (numValue == (long) numValue) {
                            yield String.valueOf((long) numValue);
                        } else {
                            yield String.valueOf(numValue);
                        }
                    }
                }
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    try {
                        yield cell.getStringCellValue();
                    } catch (Exception e) {
                        yield cell.getCellFormula();
                    }
                }
                case BLANK -> "";
                default -> "";
            };
        } catch (Exception e) {
            System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + " błąd odczytu komórki jako tekst: " + e.getMessage());
            return "";
        }
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

    private LocalDate getCellValueAsDateSafe(Cell cell, String fileName, String sheetName, int rowNumber) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                if (!dateStr.isEmpty()) {
                    return null;
                }
            }

        } catch (Exception e) {
            System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + "błąd parsowania daty w komórce: " + e.getMessage());
        }

        return null;
    }


    private Float getCellValueAsFloatSafe(Cell cell, String fileName, String sheetName, int rowNumber) {
        if (cell == null) return null;

        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> (float) cell.getNumericCellValue();
                case STRING -> {
                    String strValue = cell.getStringCellValue().trim();
                    if (strValue.isEmpty()) {
                        yield null;
                    }
                    try {
                        strValue = strValue.replace(",", ".");
                        yield Float.parseFloat(strValue);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + ": nie można sparsować '" + strValue + "' jako liczba");
                        yield null;
                    }
                }
                case FORMULA -> {
                    try {
                        yield (float) cell.getNumericCellValue();
                    } catch (Exception e) {
                        yield null;
                    }
                }
                default -> null;
            };
        } catch (Exception e) {
            System.out.println("⚠ Plik " + fileName +  " arkusz " + sheetName + " wiersz " + rowNumber + " błąd odczytu komórki jako liczba: " + e.getMessage());
            return null;
        }
    }

    private static class FilePathInfo {
        private final String projectName;
        private final String year;
        private final String month;
        private final String employeeName;
        private final String fileName;

        public FilePathInfo(String filePath) {
            this.fileName = new File(filePath).getName();
            this.employeeName = extractEmployeeNameFromFileName(fileName);

            String[] pathParts = filePath.replace("\\", "/").split("/");

            String tempProject = null;
            String tempYear = null;
            String tempMonth = null;

            for (int i = 0; i < pathParts.length - 3; i++) {
                if (pathParts[i].equals("sample-data") && i + 3 < pathParts.length) {
                    tempProject = pathParts[i + 1];  // Projekt1
                    tempYear = pathParts[i + 2];     // 2022
                    tempMonth = pathParts[i + 3];    // 01
                    break;
                }
            }

            this.projectName = tempProject != null ? tempProject : "Unknown Project";
            this.year = tempYear;
            this.month = tempMonth;
        }

        private String extractEmployeeNameFromFileName(String fileName) {
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                fileName = fileName.substring(0, lastDot);
            }

            if (fileName.startsWith("Error_")) {
                return fileName.substring(6); // "Error_Adam" -> "Adam"
            }

            if (fileName.contains("_")) {
                String[] parts = fileName.split("_");
                if (parts.length >= 2) {
                    return parts[1] + " " + parts[0]; // "Adam Nowak"
                }
            }

            return fileName.replace("_", " ");
        }

        // Gettery
        public String getProjectName() { return projectName; }
        public String getYear() { return year; }
        public String getMonth() { return month; }
        public String getEmployeeName() { return employeeName; }
        public String getFileName() { return fileName; }

        public boolean hasValidStructure() {
            return projectName != null && year != null && month != null;
        }

        @Override
        public String toString() {
            return String.format("Project: %s, Year: %s, Month: %s, Employee: %s",
                    projectName, year, month, employeeName);
        }
    }

    public static class LoadResult {
        private final Set<Employee> employees;
        private final Set<Project> projects;
        private final ArrayList<Task> tasks;
        private final List<String> processingErrors; // Nowe pole

        public LoadResult(Set<Employee> employees, Set<Project> projects, ArrayList<Task> tasks) {
            this(employees, projects, tasks, new ArrayList<>());
        }

        public LoadResult(Set<Employee> employees, Set<Project> projects, ArrayList<Task> tasks, List<String> processingErrors) {
            this.employees = employees;
            this.projects = projects;
            this.tasks = tasks;
            this.processingErrors = processingErrors != null ? processingErrors : new ArrayList<>();

        }

        public Set<Employee> getEmployees() { return employees; }
        public Set<Project> getProjects() { return projects; }
        public ArrayList<Task> getTasks() { return new ArrayList<>(tasks); }
        public List<String> getProcessingErrors() { return new ArrayList<>(processingErrors); }

        // Przydatne metody dla raportowania
        public int getTotalEmployees() { return employees.size(); }
        public int getTotalProjects() { return projects.size(); }
        public int getTotalTasks() { return tasks.size(); }
        public int getTotalErrors() { return processingErrors.size(); }
        public boolean hasErrors() { return !processingErrors.isEmpty(); }


        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("📊 Podsumowanie: %d zadań, %d pracowników, %d projektów",
                    tasks.size(), employees.size(), projects.size()));

            if (hasErrors()) {
                summary.append(String.format(" (⚠ %d błędów)", processingErrors.size()));
            }

            return summary.toString();
        }

        public String getErrorReport() {
            if (processingErrors.isEmpty()) {
                return "✅ Brak błędów podczas przetwarzania";
            }

            StringBuilder report = new StringBuilder();
            report.append("❌ Wystąpiły następujące błędy:\n");
            for (int i = 0; i < processingErrors.size(); i++) {
                report.append(String.format("%d. %s\n", i + 1, processingErrors.get(i)));
            }

            return report.toString();
        }

        @Override
        public String toString() {
            return getSummary();
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