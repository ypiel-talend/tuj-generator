package com.talend.tuj.generator.conf;

import org.apache.commons.cli.*;

public class ArgsHandler {
    public static TUJGeneratorConfiguration handle(String[] args){

        Options options = new Options();

        Option input = new Option("i", "input", true, "input TUJ folder");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "input TUJ folder");
        output.setRequired(true);
        options.addOption(output);

        // TODO Can be ignored as Colibri overwrites contexts
        Option contextSubstitution = new Option("s", "context-substitution", true, "input TUJ folder");
        contextSubstitution.setRequired(false);
        options.addOption(contextSubstitution);

        Option fileSubstitution = new Option("S", "file-substitution", true, "input TUJ folder");
        fileSubstitution.setRequired(false);
        options.addOption(fileSubstitution);

        Option distributionName = new Option("N", "distribution-name", true, "input TUJ folder");
        distributionName.setRequired(true);
        options.addOption(distributionName);

        Option distributionVersion = new Option("V", "distribution-version", true, "input TUJ folder");
        distributionVersion.setRequired(true);
        options.addOption(distributionVersion);

        Option tuj = new Option("t", "tuj", true, "input TUJ folder");
        tuj.setRequired(false);
        options.addOption(tuj);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return null;
        }

        TUJGeneratorConfiguration conf = new TUJGeneratorConfiguration();
        conf.put("input", cmd.getOptionValue('i'));
        conf.put("output", cmd.getOptionValue('o'));
        conf.put("distributionName", cmd.getOptionValue('N'));
        conf.put("distributionValue", cmd.getOptionValue('V'));
        if(cmd.hasOption('t')) conf.put("tuj", cmd.getOptionValue('t'));
        if(cmd.hasOption('S')) conf.put("fileSubstitution", cmd.getOptionValue('S'));
        if(cmd.hasOption('s')) conf.put("contextSubstitution", cmd.getOptionValue('s'));
        return conf;
    }
}
