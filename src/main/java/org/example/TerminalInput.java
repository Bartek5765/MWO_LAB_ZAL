package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TerminalInput {
    private final String reportType;
    private final YearMonth date;
    private final Path rootPath;

    public static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public TerminalInput(String reportType, YearMonth date, Path rootPath) {
        this.reportType = reportType;
        this.date = date;
        this.rootPath = rootPath;
    }

    public String getReportType() {
        return reportType;
    }

    public YearMonth getDate() {
        return date;
    }

    public Path getRootPath() {
        return rootPath;
    }

    // odczyt z konsoli
    public static TerminalInput readFromConsole() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Podaj typ raportu - EMPLOYEE lub PROJECT");
        String reportType = sc.nextLine().trim();

        YearMonth ym = null;
        while (ym == null) {
            System.out.print("Podaj datę w formacie RRRR-MM");
            String dateStr = sc.nextLine().trim();
            try {
                ym = YearMonth.parse(dateStr, YM_FORMATTER);
            } catch (DateTimeException e) {
                System.out.println("Błędny format daty. Sþróbuj ponownie.");
            }
        }
        System.out.print("Podaj ścieżkę do głównego katalogu");
        String pathStr = sc.nextLine().trim();
        Path rootPath = Paths.get(pathStr);

        return new TerminalInput(reportType, ym, rootPath);
    }
    // args[0] = typ raportu
    //args [1] = data RRRR-MM
    // args[2] = ścieżka katalogu

    public static TerminalInput fromArgs(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Użycie: java App <reportType> <YYYY-MM> <rootpath>");
        }
        String reportType = args[0];

        YearMonth ym;
        try {
            ym = YearMonth.parse(args[1], YM_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data musi być w formacie RRRR-MM", e);
        }
        Path rootPath = Paths.get(args[2]);
        return new TerminalInput(reportType, ym, rootPath);
    }
}
