package org.example;

public class ReportPrinter {

    public String printToTerminal(IReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nReport:" + report.getName() + "\n");
        sb.append("\n");
        sb.append(report.getReportString());
        sb.append("\n");
        System.out.println(sb.toString());
        return sb.toString();
    }
}