package org.example;

import org.apache.commons.cli.*;

public class CliOptions {

    public static CommandLine parse(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("r", "report type", true, "report type: employees, projects or top number");
        options.addOption("p", "path", true, "path to directory or file");
        options.addOption("df", "date from", true, "date from");
        options.addOption("dt", "date to", true, "date to");
        options.addOption("top", "top number", true, "top number");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
