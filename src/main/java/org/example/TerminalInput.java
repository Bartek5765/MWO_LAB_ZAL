package org.example;

import org.apache.commons.cli.CommandLine;

import java.time.YearMonth;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


 // Klasa odpowiadająca za parsowanie parametrów wejściowych programu.
 // Użycie:
 // java -jar raport.jar -r <reportType> -p <ścieżka_katalogu> -df <RRRR-MM-DD> -dt <RRRR-MM-DD>

public class TerminalInput {
    private ReportType reportType;
    private String rootPath;
    private LocalDate fromDate;
    private LocalDate toDate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TerminalInput() {}

    private TerminalInput(ReportType reportType,
                          String rootPath,
                          LocalDate fromDate,
                          LocalDate toDate) {
        this.reportType = reportType;
        this.rootPath = rootPath;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void fromArgs(CommandLine args) {

        if (args == null ) {
            throw new IllegalArgumentException(
                    "Użycie: java -jar raport.jar -r <employees|projects> -p <ścieżka_katalogu> -df <RRRR-MM-DD> -dt <RRRR-MM-DD>");
        }
        reportType = null;
        if (args.hasOption("r")) {

            try {
                reportType = ReportType.valueOf(args.getOptionValue("r"));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Nieznany typ raportu: " + args.getOptionValue("r") + ". Dostępne: employees, projects", e);
            }
        }

        rootPath = null;
        if (args.hasOption("p")) {
            rootPath = args.getOptionValue("p");
            try {
                rootPath = args.getOptionValue("p");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            }
            if (rootPath == null || rootPath.isEmpty()) {
                throw new IllegalArgumentException("Parametr ścieżki jest wymagany");
            }
        }
        LocalDate fromDate = null;
        LocalDate toDate = null;

        if (!args.hasOption("df") || !args.hasOption("dt")) {
            throw new IllegalArgumentException("muszą być podane obie daty");
        }

            try {
                fromDate = LocalDate.parse(args.getOptionValue("df"), DATE_FORMATTER);
                toDate = LocalDate.parse(args.getOptionValue("dt"), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "Daty muszą być w formacie RRRR-MM-DD", e);
            }
            if (fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException(
                        "Data początkowa nie może być po dacie końcowej");
            }


        this.reportType = reportType;
        this.rootPath = rootPath;
        this.fromDate = fromDate;
        this.toDate = toDate;

    }

    // Gettery
    public ReportType getReportType() { return reportType; }
    public String getRootPath()     { return rootPath;    }
//    public YearMonth getPeriod()    { return period;      }
    public LocalDate getFromDate()  { return fromDate;    }
    public LocalDate getToDate()    { return toDate;      }
}