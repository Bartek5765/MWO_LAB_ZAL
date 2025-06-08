package org.example;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;



class TerminalInputTest {

    @Test
    void testFromArgsValidEmployeesInput() {
        String[] args = {"C:/reports", "employees", "2023-04-01"};
        TerminalInput input = TerminalInput.fromArgs(args);

        assertEquals("C:/reports", input.getRootPath());
        assertEquals(ReportType.employees, input.getReportType());
        assertEquals(YearMonth.of(2023, 4), input.getDate());
    }

    @Test
    void testFromArgsValidProjectsInput() {
        String[] args = {"/home/user", "projects", "2022-12-01"};
        TerminalInput input = TerminalInput.fromArgs(args);

        assertEquals("/home/user", input.getRootPath());
        assertEquals(ReportType.projects, input.getReportType());
        assertEquals(YearMonth.of(2022, 12), input.getDate());
    }

    @Test
    void testFromArgsInvalidArgumentLength() {
        String[] args = {"C:/data", "employees"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TerminalInput.fromArgs(args);
        });

        assertEquals("Użycie: java App <rootpath> <reportType> <YYYY-MM-DD>", exception.getMessage());
    }

    @Test
    void testFromArgsInvalidReportType() {
        String[] args = {"C:/data", "invalidType", "2023-01-01"};


        TerminalInput input = TerminalInput.fromArgs(args);
        assertEquals(ReportType.error, input.getReportType());
    }

    @Test
    void testFromArgsInvalidDateFormat() {
        String[] args = {"C:/data", "employees", "2023/01/01"};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TerminalInput.fromArgs(args);
        });

        assertTrue(exception.getMessage().contains("Data musi być w formacie RRRR-MM"));
    }
}
