package org.example;

import java.util.ArrayList;
import java.util.List;

public class ReportPrinter {

    public String printToTerminal(IReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nReport: ").append(report.getName()).append("\n\n");

        String reportData = report.getReportString();
        String[] rows = reportData.split("\n");
        List<String[]> table = new ArrayList<>();

        int columnCount = 0;
        for (String row : rows) {
            String[] columns = row.split("\t");
            table.add(columns);
            columnCount = Math.max(columnCount, columns.length);
        }

        // Calculate max width for each column
        int[] columnWidths = new int[columnCount];
        for (String[] row : table) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }

        // Build formatted table
        for (String[] row : table) {
            for (int i = 0; i < columnCount; i++) {
                String cell = (i < row.length) ? row[i] : "";
                sb.append(String.format("%-" + (columnWidths[i] + 2) + "s", cell));
            }
            sb.append("\n");
        }

        String output = sb.toString();
        System.out.println(output);
        return output;
    }
}