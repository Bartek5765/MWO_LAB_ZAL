package org.example;

public class ReportPrinter {

    public String printToTerminal(IReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n Report:");
        sb.append(report.getReportString());
        sb.append("\n");
        return sb.toString();
    }
}