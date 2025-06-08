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


    //      Ładuje dane ze wszystkich plików Excel w katalogu
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

        System.out.println("Znaleziono " + excelFiles.size() + " plików Excel");

        Set<Employee> allEmployees = new HashSet<>();
        Set<Project> allProjects = new HashSet<>();
        List<Task> allTasks = new ArrayList<>();
        List<String> processingErrors = new ArrayList<>(); // Zbieranie błędów

        for (String filePath : excelFiles) {
            try {
                System.out.println("Przetwarzanie pliku: " + filePath);
                LoadResult result = loadDataFromFile(filePath);

                allEmployees.addAll(result.getEmployees());
                allProjects.addAll(result.getProjects());
                allTasks.addAll(result.getTasks());

                System.out.println("✓ Pomyślnie przetworzono plik: " + filePath +
                        " (" + result.getTasks().size() + " zadań)");

            } catch (Exception e) {
                String errorMsg = "Błąd w pliku " + filePath + ": " + e.getMessage();
                processingErrors.add(errorMsg);
                System.err.println("⚠ " + errorMsg);
                // Kontynuuj z pozostałymi plikami zamiast przerywać
            }
        }

        // Jeśli żaden plik się nie przetworzył pomyślnie
        if (allTasks.isEmpty() && !processingErrors.isEmpty()) {
            throw new ExcelLoadException("Nie udało się przetworzyć żadnego pliku. Błędy: " +
                    String.join("; ", processingErrors));
        }

        System.out.println("🎉 Zakończono przetwarzanie. Łącznie: " +
                allTasks.size() + " zadań z " + excelFiles.size() + " plików");

        if (!processingErrors.isEmpty()) {
            System.out.println("⚠ Wystąpiły błędy w " + processingErrors.size() + " plikach");
        }

        return new LoadResult(allEmployees, allProjects, new ArrayList<>(allTasks), processingErrors);
    }


    // Znajduje wszystkie pliki Excel w katalogu (rekurencyjnie lub nie)
    private List<String> findExcelFiles(String directoryPath) throws IOException {
        List<String> excelFiles = new ArrayList<>();
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir)) {
            throw new IOException("Katalog nie istnieje: " + directoryPath);
        }

        if (!Files.isDirectory(dir)) {
            throw new IOException("Podana ścieżka nie jest katalogiem: " + directoryPath);
        }

        // Skanowanie tylko w głównym katalogu (bez rekurencji)
        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> isExcelFile(path.toString()))
                    .map(Path::toString)
                    .forEach(excelFiles::add);
        }

        return excelFiles;
    }

    //Alternatywna metoda dla skanowania rekurencyjnego
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

    // Sprawdza czy plik jest plikiem Excel
    private boolean isExcelFile(String fileName) {
        String lowercaseName = fileName.toLowerCase();
        return lowercaseName.endsWith(".xls") || lowercaseName.endsWith(".xlsx");
    }

    public LoadResult loadDataFromFile(String filePath) throws IOException, ExcelLoadException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new HSSFWorkbook(fis)) {

            System.out.println("Workbook utworzony pomyślnie dla: " + filePath);

            // NOWE: Użyj FilePathInfo
            FilePathInfo pathInfo = new FilePathInfo(filePath);
            System.out.println("📁 " + pathInfo.toString());

            if (!pathInfo.hasValidStructure()) {
                System.out.println("⚠ Nieprawidłowa struktura ścieżki: " + filePath);
            }

            Set<Employee> employees = new HashSet<>();
            Set<Project> projects = new HashSet<>();
            List<Task> tasks = new ArrayList<>();
            List<String> skippedRows = new ArrayList<>(); // Tracking pominiętych wierszy

            // ZMIANA: Użyj nazwy z pathInfo zamiast z pliku
            Employee employee = getOrCreateEmployee(pathInfo.getEmployeeName());

            for (Sheet sheet : workbook) {
                // ZMIANA: Używaj nazwy projektu ze struktury folderów, nie z arkusza
                String projectName = pathInfo.hasValidStructure() ?
                        pathInfo.getProjectName() :
                        sheet.getSheetName(); // fallback na nazwę arkusza

                Project project = getOrCreateProject(projectName);

                System.out.println("Przetwarzanie arkusza: " + sheet.getSheetName() +
                        " dla projektu: " + projectName);

                // Sprawdź czy arkusz ma jakiekolwiek dane
                if (sheet.getLastRowNum() < 0) {
                    System.out.println("⚠ Arkusz " + sheet.getSheetName() + " jest pusty - pomijam");
                    continue;
                }

                Row headerRow = sheet.getRow(0);
                if (headerRow == null || isEmptyRow(headerRow)) {
                    System.out.println("⚠ Arkusz " + sheet.getSheetName() + " nie ma nagłówków - pomijam");
                    continue;
                }

                // Walidacja nagłówków - nie przerywaj jeśli błędne, tylko loguj
                try {
                    validateHeaders(headerRow);
                } catch (ExcelLoadException e) {
                    System.out.println("⚠ Błędne nagłówki w arkuszu " + sheet.getSheetName() + ": " + e.getMessage());
                    // Kontynuuj - może dane są w innym formacie ale da się je odczytać
                }

                int validTasksInSheet = 0;

                // Przetwarzaj wiersze danych (od wiersza 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);

                    // Pomiń całkowicie puste wiersze
                    if (row == null || isEmptyRow(row)) {
                        continue;
                    }

                    try {
                        Task taskData = parseRowSafe(row, i + 1, sheet.getSheetName());

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
                        // Kontynuuj z następnym wierszem
                    }
                }

                System.out.println("✓ Arkusz '" + sheet.getSheetName() + "': " +
                        validTasksInSheet + " prawidłowych zadań");
            }

            if (!skippedRows.isEmpty()) {
                System.out.println("📊 Pominięto " + skippedRows.size() + " problematycznych wierszy");
            }

            return new LoadResult(employees, projects, new ArrayList<>(tasks), skippedRows);

        } catch (IOException e) {
            throw new IOException("Nie można odczytać pliku: " + filePath, e);
        }
    }

    // Pobiera lub tworzy pracownika z cache
    private Employee getOrCreateEmployee(String employeeName) {
        return employeeCache.computeIfAbsent(employeeName, Employee::new);
    }

    // Pobiera lub tworzy projekt z cache

    private Project getOrCreateProject(String projectName) {
        return projectCache.computeIfAbsent(projectName, Project::new);
    }


    // Pozostałe metody pomocnicze pozostają bez zmian
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


    // Bezpieczne parsowanie wiersza - nie rzuca wyjątków, zwraca null dla nieprawidłowych danych
    private Task parseRowSafe(Row row, int rowNumber, String sheetName) {
        try {
            // Sprawdź czy wiersz ma wystarczająco komórek
            if (row.getLastCellNum() < 3) {
                System.out.println("⚠ Wiersz " + rowNumber + " ma za mało komórek (< 3)");
                return null;
            }

            LocalDate date = getCellValueAsDateSafe(row.getCell(0));
            String taskName = getCellValueAsStringSafe(row.getCell(1));
            Float duration = getCellValueAsFloatSafe(row.getCell(2));

            // Walidacja z elastycznością
            if (taskName == null || taskName.trim().isEmpty()) {
                System.out.println("⚠ Wiersz " + rowNumber + ": pusta nazwa zadania - pomijam");
                return null;
            }

            if (date == null) {
                System.out.println("⚠ Wiersz " + rowNumber + ": nieprawidłowa data - pomijam");
                return null;
            }

            if (duration == null || duration <= 0) {
                System.out.println("⚠ Wiersz " + rowNumber + ": nieprawidłowy czas (" + duration + ") - pomijam");
                return null;
            }

            return new Task(date, taskName.trim(), duration);

        } catch (Exception e) {
            System.out.println("⚠ Błąd parsowania wiersza " + rowNumber + ": " + e.getMessage());
            return null;
        }
    }

    private Task parseRow(Row row, int rowNumber) throws ExcelLoadException {
        try {
            LocalDate date = getCellValueAsDate(row.getCell(0));
            String taskName = getCellValueAsString(row.getCell(1)).trim();
            Float duration = getCellValueAsFloat(row.getCell(2));

            if (taskName.isEmpty()) {
                throw new ExcelLoadException("Nazwa zadania nie może być pusta");
            }
            if (date == null) {
                throw new ExcelLoadException("Data nie może być pusta");
            }
            if (duration == null || duration <= 0) {
                throw new ExcelLoadException("Czas trwania musi być liczbą większą od 0");
            }

            System.out.println(taskName + " " + date + " " + duration);

            return new Task(date, taskName, duration);

        } catch (Exception e) {
            throw new ExcelLoadException("Błąd parsowania wiersza: " + e.getMessage());
        }
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;

        // Sprawdź wszystkie komórki w wierszu (nie tylko pierwsze 5)
        int lastCellNum = row.getLastCellNum();
        if (lastCellNum <= 0) return true;

        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValueAsStringSafe(cell);
            if (cellValue != null && !cellValue.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }


    // Bezpieczna wersja getCellValueAsString - nigdy nie rzuca wyjątków

    private String getCellValueAsStringSafe(Cell cell) {
        if (cell == null) return "";

        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield cell.getDateCellValue().toString();
                    } else {
                        // Sprawdź czy to liczba całkowita czy dziesiętna
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
                        // Spróbuj pobrać obliczoną wartość formuły
                        yield cell.getStringCellValue();
                    } catch (Exception e) {
                        yield cell.getCellFormula();
                    }
                }
                case BLANK -> "";
                default -> "";
            };
        } catch (Exception e) {
            System.out.println("⚠ Błąd odczytu komórki jako tekst: " + e.getMessage());
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

    // Bezpieczna wersja getCellValueAsDate - nie rzuca wyjątków

    private LocalDate getCellValueAsDateSafe(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            // Spróbuj sparsować tekst jako datę
            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                if (!dateStr.isEmpty()) {
                    // Można dodać parsowanie różnych formatów daty
                    // Na razie zwracamy null dla tekstowych dat
                    return null;
                }
            }

        } catch (Exception e) {
            System.out.println("⚠ Błąd parsowania daty w komórce: " + e.getMessage());
        }

        return null;
    }


    // Bezpieczna wersja getCellValueAsFloat - nie rzuca wyjątków
    private Float getCellValueAsFloatSafe(Cell cell) {
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
                        // Obsługa różnych formatów liczb (z przecinkiem, etc.)
                        strValue = strValue.replace(",", ".");
                        yield Float.parseFloat(strValue);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠ Nie można sparsować '" + strValue + "' jako liczba");
                        yield null;
                    }
                }
                case FORMULA -> {
                    try {
                        // Spróbuj pobrać obliczoną wartość liczbową formuły
                        yield (float) cell.getNumericCellValue();
                    } catch (Exception e) {
                        yield null;
                    }
                }
                default -> null;
            };
        } catch (Exception e) {
            System.out.println("⚠ Błąd odczytu komórki jako liczba: " + e.getMessage());
            return null;
        }
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


    // Klasa pomocnicza do parsowania informacji ze ścieżki pliku
    private static class FilePathInfo {
        private final String projectName;
        private final String year;
        private final String month;
        private final String employeeName;
        private final String fileName;

        public FilePathInfo(String filePath) {
            this.fileName = new File(filePath).getName();
            this.employeeName = extractEmployeeNameFromFileName(fileName);

            // Parsowanie ścieżki: sample-data/Projekt1/2022/01/Nowak_Adam.xls
            String[] pathParts = filePath.replace("\\", "/").split("/");

            String tempProject = null;
            String tempYear = null;
            String tempMonth = null;

            // Szukaj wzorca: .../sample-data/ProjektX/YYYY/MM/...
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
            // Usuń rozszerzenie
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                fileName = fileName.substring(0, lastDot);
            }

            // Obsłuż pliki Error_*
            if (fileName.startsWith("Error_")) {
                return fileName.substring(6); // "Error_Adam" -> "Adam"
            }

            // Standardowe pliki: "Nowak_Adam" -> "Adam Nowak"
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

    // LoadResult i ExcelLoadException z dodaniem informacji o błędach
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

            for (Task task : tasks) {
                System.out.println(task.getName());
            }
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


        // Zwraca podsumowanie do raportowania
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("📊 Podsumowanie: %d zadań, %d pracowników, %d projektów",
                    tasks.size(), employees.size(), projects.size()));

            if (hasErrors()) {
                summary.append(String.format(" (⚠ %d błędów)", processingErrors.size()));
            }

            return summary.toString();
        }

        // Zwraca szczegółowy raport błędów
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