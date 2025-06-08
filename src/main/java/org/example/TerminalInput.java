package org.example;

import java.time.YearMonth;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


 // Klasa odpowiadająca za parsowanie parametrów wejściowych programu.
 // Użycie:
 // java -jar raport.jar <reportType> <ścieżka_katalogu> <RRRR-MM-DD> <RRRR-MM-DD>

public class TerminalInput {
    private ReportType reportType;
    private String rootPath;
//    private final YearMonth period;
    private LocalDate fromDate;
    private LocalDate toDate;

    // Formatter dla okresu (rok-miesiąc)
//    private static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    // Formatter dla zakresu dat (rok-miesiąc-dzień)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TerminalInput() {}

    private TerminalInput(ReportType reportType,
                          String rootPath,
//                          YearMonth period,
                          LocalDate fromDate,
                          LocalDate toDate) {
        this.reportType = reportType;
        this.rootPath = rootPath;
//        this.period = period;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

     // Tworzymy instancję TerminalInput na podstawie argumentów przekazanych do programu.
     // @param args tablica argumentów
     // @return obiekt z sparsowanymi parametrami
     // @throws IllegalArgumentException w przypadku niepoprawnych parametrów

    public void fromArgs(String[] args) {
        if (args == null || args.length < 3) {
            throw new IllegalArgumentException(
                    "Użycie: java -jar raport.jar <employees|projects> <ścieżka_katalogu> <RRRR-MM-DD> <RRRR-MM-DD>");
        }
        // Parsowanie typu raportu
        ReportType rt;
        try {
            rt = ReportType.valueOf(args[0]);//.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Nieznany typ raportu: " + args[0] + ". Dostępne: EMPLOYEES, PROJECTS", e);
        }
        // Ścieżka do katalogu z plikami
        String rootPath = args[1];

//        // Parsowanie okresu (rok-miesiąc)
//        YearMonth ym;
//        try {
//            ym = YearMonth.parse(args[2], YM_FORMATTER);
//        } catch (DateTimeParseException e) {
//            throw new IllegalArgumentException("Okres musi być w formacie RRRR-MM", e);
//        }

        // Opcjonalny zakres dat od-do
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (args.length <= 4) {
            try {
                fromDate = LocalDate.parse(args[2], DATE_FORMATTER);
                toDate = LocalDate.parse(args[3], DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "Daty muszą być w formacie RRRR-MM-DD", e);
            }
            if (fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException(
                        "Data początkowa nie może być po dacie końcowej");
            }
        }

//        return new TerminalInput(rt, rootPath, ym, fromDate, toDate);

        this.reportType = rt;
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