package com.talend.tuj.generator.io;

import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.TUJ;
import com.talend.tuj.generator.conf.TUJGeneratorConfiguration;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TUJExporter {
    private static TransformerFactory tFactory = TransformerFactory.newInstance();

    public static void exportTUJ(TUJGeneratorConfiguration conf, List<TUJ> tujs){
        for(TUJ tuj : tujs){
            writeTUJ(tuj, conf);
        }
    }

    private static void writeTUJ(TUJ tuj, TUJGeneratorConfiguration conf){
        Path root = FileSystems.getDefault().getPath(conf.get("output"), tuj.getName(), tuj.getProjectName());
        root.toFile().mkdirs();

        // write resources here
        try {
            for (Path resource : tuj.getResources()){
                Files.copy(resource, root.resolve(resource.getFileName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Job> jobs = tuj.getStarterJob().getChildJobs();
        jobs.add(tuj.getStarterJob());

        try {
            Transformer transformer = tFactory.newTransformer();

            for (Job job : jobs){
                String processFolder = "process";

                switch (job.getType()){
                    case BIG_DATA_BATCH:
                        processFolder += "_mr";
                        break;
                    case BIG_DATA_STREAMING:
                        processFolder += "_storm";
                        break;
                }

                Path jobFolder = root.resolve(processFolder).resolve(job.getFsPath().orElse(Paths.get("")));
                jobFolder.toFile().mkdirs();

                File itemFile = jobFolder.resolve(job.getName()+"_"+job.getVersion()+".item").toFile();
                itemFile.createNewFile();
                transformer.transform(new DOMSource(job.getItem()), new StreamResult(new FileWriter(itemFile)));

                File propertiesFile = jobFolder.resolve(job.getName()+"_"+job.getVersion()+".properties").toFile();
                propertiesFile.createNewFile();
                transformer.transform(new DOMSource(job.getProperties()), new StreamResult(new FileWriter(propertiesFile)));

                File screenshotFile = jobFolder.resolve(job.getName()+"_"+job.getVersion()+".screenshot").toFile();
                screenshotFile.createNewFile();
                transformer.transform(new DOMSource(job.getScreenshot()), new StreamResult(new FileWriter(screenshotFile)));

            }

        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }


    }


}
