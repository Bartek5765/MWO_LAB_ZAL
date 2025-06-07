package org.example;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalInputTest {

    @Test
    public void testConstructorAndGetters() {
        ReportType reportType = ReportType.employees;
        LocalDate date = LocalDate.of(2024, 5, 20);
        String rootPath = "/path/to/root";

        TerminalInput input = new TerminalInput(reportType, date, rootPath);

        assertEquals(reportType, input.getReportType());
        assertEquals(date, input.getDate());
        assertEquals(rootPath, input.getRootPath());
    }
//
//    @Test
//    public void testFromArgsValidInput() {
//        String[] args = {ReportType.projects, "2023-11", "/some/path"};
//
//        TerminalInput input = TerminalInput.fromArgs(args);
//
//        assertEquals("PROJECT", input.getReportType());
//        assertEquals(YearMonth.of(2023, 11), input.getDate());
//        assertEquals(Path.of("/some/path"), input.getRootPath());
//    }

//    @Test
//    public void testFromArgsInvalidArgsLength() {
//        String[] args = {ReportType.employees, "2023-05"};
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            TerminalInput.fromArgs(args);
//        });
//
//        assertTrue(exception.getMessage().contains("Użycie"));
//    }

//    @Test
//    public void testFromArgsInvalidDateFormat() {
//        String[] args = {"PROJECT", "2023/05", "/some/path"};
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            TerminalInput.fromArgs(args);
//        });
//
//        assertTrue(exception.getMessage().contains("Data musi być w formacie RRRR-MM"));
//    }
}