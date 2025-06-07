package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TerminalInput {
    private ReportType reportType;
    private YearMonth date;
    private String rootPath;

    public static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public TerminalInput() {}
    public TerminalInput(ReportType reportType, YearMonth date, String rootPath) {
        this.reportType = reportType;
        this.date = date;
        this.rootPath = rootPath;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public YearMonth getDate() {
        return date;
    }

    public String getRootPath() {
        return rootPath;
    }

    // odczyt z konsoli
    public static TerminalInput readFromConsole() {
        Scanner sc = new Scanner(System.in);

        ReportType reportType = ReportType.error;
        boolean isReportTypeCorrect = false;
        while (!isReportTypeCorrect) {
            System.out.print("Podaj typ raportu - employees lub projects ");
            String input = sc.nextLine().trim();
            switch (input) {
                case "employees":
                    reportType = ReportType.employees;
                    isReportTypeCorrect = true;
                    break;
                case "projects":
                    reportType = ReportType.projects;
                    isReportTypeCorrect = true;
                    break;
                default:
                    reportType = ReportType.error;
                    System.out.println("Zły typ raportu. Podaj typ raportu - employees lub projects ");
                    isReportTypeCorrect = false;
                    break;
            }
        }

        YearMonth ym = null;
        while (ym == null) {
            System.out.print("Podaj datę od w formacie RRRR-MM-DD ");
            String dateStr = sc.nextLine().trim();
            try {
                ym = YearMonth.parse(dateStr, YM_FORMATTER);
            } catch (DateTimeException e) {
                System.out.println("Błędny format daty. Sþróbuj ponownie. ");
            }
        }
        System.out.print("Podaj ścieżkę do głównego katalogu ");
        String rootPath = sc.nextLine().trim();

        return new TerminalInput(reportType, ym, rootPath);
    }

    // args[0] = typ raportu
    //args [1] = data RRRR-MM
    // args[2] = ścieżka katalogu

    public static TerminalInput fromArgs(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Użycie: java App <rootpath> <reportType> <YYYY-MM-DD>");
        }
        String rootPath = args[0];
        String arg1 = args[1];
        ReportType reportType = ReportType.error;
        boolean isReportTypeCorrect = false;
        while (!isReportTypeCorrect) {
            switch (arg1) {
                case "employees":
                    reportType = ReportType.employees;
                    isReportTypeCorrect = true;
                    break;
                case "projects":
                    reportType = ReportType.projects;
                    isReportTypeCorrect = true;
                    break;
                default:
                    reportType = ReportType.error;
                    System.out.println("Zły typ raportu. Podaj typ raportu - employees lub projects ");
                    isReportTypeCorrect = false;
                    break;
            }
        }

        YearMonth ym;
        try {
            ym = YearMonth.parse(args[2], YM_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data musi być w formacie RRRR-MM", e);
        }
        return new TerminalInput(reportType, ym, rootPath);
    }
}
