package org.example;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;



class TerminalInputTest {

    @Test
    void testFromArgsValidEmployeesInput() {
//        String[] args = {"employees", "C:/reports", "2023-04-01", "2026-04-01"};
//        TerminalInput input = new TerminalInput();
//        input.fromArgs(args);
//
//        assertEquals("C:/reports", input.getRootPath());
//        assertEquals(ReportType.employees, input.getReportType());
//        assertEquals(YearMonth.of(2023, 4), input.getFromDate());
    }

    @Test
    void testFromArgsValidProjectsInput() {
//        String[] args = {"projects", "/home/user", "2022-12-01", "2026-04-01"};
//        TerminalInput input = new TerminalInput();
//        input.fromArgs(args);
//
//        assertEquals("/home/user", input.getRootPath());
//        assertEquals(ReportType.projects, input.getReportType());
//        assertEquals(YearMonth.of(2022, 12), input.getFromDate());
    }

    @Test
    void testFromArgsInvalidArgumentLength() {
        String[] args = {"C:/data", "employees"};

//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            TerminalInput.fromArgs(args);
//        });

//        assertEquals("Użycie: java App <rootpath> <reportType> <YYYY-MM-DD>", exception.getMessage());
    }

    @Test
    void testFromArgsInvalidReportType() {
//        String[] args = {"invalidType", "C:/data", "2023-01-01"};
//
//        TerminalInput input = new TerminalInput();
//        input.fromArgs(args);
//        assertEquals(ReportType.error, input.getReportType());
    }

    @Test
    void testFromArgsInvalidDateFormat() {
//        String[] args = {"C:/data", "employees", "2023/01/01"};
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
////            TerminalInput.fromArgs(args);
//        });
//
//        assertTrue(exception.getMessage().contains("Data musi być w formacie RRRR-MM"));
    }
}
