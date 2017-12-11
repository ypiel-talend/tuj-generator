package com.talend.tuj.generator.io;

import com.talend.tuj.generator.conf.TUJGeneratorConfiguration;
import com.talend.tuj.generator.exception.NotWellMadeTUJException;
import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.TUJ;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class TUJImporter {
    private static final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private Optional<Pattern> pattern = Optional.empty();


    public List<TUJ> importTUJ(TUJGeneratorConfiguration conf) {
        Path root = FileSystems.getDefault().getPath(conf.get("input"));

        if (conf.containsKey("tuj")) {
            pattern = Optional.of(Pattern.compile(conf.get("tuj")));
        }
        return findTUJsInFolder(root);
    }

    private Job generateJobHierarchy(List<Job> jobList) throws NotWellMadeTUJException {
        Map<String, Job> jobMap = new HashMap<>();

        for (Job job : jobList) {
            jobMap.put(job.getId(), job);
        }

        return generateJobHierarchy(jobMap);
    }

    private Job generateJobHierarchy(Map<String, Job> jobMap) throws NotWellMadeTUJException {
        Map<String, List<String>> jobHierarchy = new HashMap<>();

        for (Job job : jobMap.values()) {
            List<String> childs = job.findChildjobs();
            jobHierarchy.put(job.getId(), childs);
            for (String child : childs) {
                job.addChildJob(jobMap.get(child));
            }
        }

        Set<String> jobIds = jobMap.keySet();
        for (List<String> childs : jobHierarchy.values()) {
            jobIds.removeAll(childs);
        }

        if (jobIds.size() == 1) {
            return jobMap.get(jobIds.iterator().next());
        }

        throw new NotWellMadeTUJException();
    }

    private List<TUJ> findTUJsInFolder(Path root) {
        List<TUJ> tujs = new ArrayList<>();

        try (DirectoryStream<Path> rootFolder = Files.newDirectoryStream(root)) {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            for (Path tujRoot : rootFolder) {
                if (Files.isDirectory(tujRoot) &&
                        (!pattern.isPresent() ||
                                (pattern.isPresent() && pattern.get().matcher(tujRoot.getFileName().toString()).find())
                        )) {
                    String projectName = findProjectName(tujRoot);

                    List<Job> jobs = new ArrayList<>();

                    if (Files.isDirectory(tujRoot.resolve(projectName).resolve("process"))) {
                        jobs.addAll(findJobsInFolder(tujRoot.resolve(projectName).resolve("process")));
                    } else {
                        //System.err.println("No DI folder");
                    }

                    if (Files.isDirectory(tujRoot.resolve(projectName).resolve("process_mr"))) {
                        jobs.addAll(findJobsInFolder(tujRoot.resolve(projectName).resolve("process_mr")));
                    } else {
                        //System.err.println("No Batch folder");
                    }

                    if (Files.isDirectory(tujRoot.resolve(projectName).resolve("process_storm"))) {
                        jobs.addAll(findJobsInFolder(tujRoot.resolve(projectName).resolve("process_storm")));
                    } else {
                        //System.err.println("No Streaming folder");
                    }
                    try {
                        tujs.add(new TUJ(
                                generateJobHierarchy(jobs),
                                dBuilder.parse(tujRoot.resolve(projectName).resolve("talend.project").toString()),
                                findResourcesInFolder(tujRoot),
                                tujRoot.getFileName().toString(),
                                projectName
                        ));
                    } catch (NotWellMadeTUJException e) {
                        System.err.println("Ignoring TUJ : " + tujRoot.getFileName().toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return tujs;
    }

    private List<Job> findJobsInFolder(Path root) {
        return findJobsInFolder(root, Optional.empty());
    }

    private List<Job> findJobsInFolder(Path root, Optional<Path> folderStructure) {
        ArrayList<Job> jobs = new ArrayList<>();
        Set<String> jobNames = new HashSet<>();

        try (DirectoryStream<Path> rootFolder = Files.newDirectoryStream(root)) {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            for (Path folder : rootFolder) {
                if (Files.isDirectory(folder)) {
                    if (folderStructure.isPresent())
                        jobs.addAll(findJobsInFolder(folder, Optional.of(folderStructure.get().resolve(folder.getFileName().toString()))));
                    else jobs.addAll(findJobsInFolder(folder, Optional.of(folder.getFileName())));
                } else {
                    String jobName = folder.getFileName().toString();
                    jobNames.add(jobName.substring(0, jobName.lastIndexOf('.')));
                }
            }

            for (String jobName : jobNames) {
                jobs.add(new Job(
                        dBuilder.parse(root.resolve(jobName + ".properties").toString()),
                        dBuilder.parse(root.resolve(jobName + ".item").toString()),
                        dBuilder.parse(root.resolve(jobName + ".screenshot").toString()),
                        folderStructure
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    private List<Path> findResourcesInFolder(Path root) {
        List<Path> resources = new ArrayList<>();

        try (DirectoryStream<Path> rootFolder = Files.newDirectoryStream(root)) {
            for (Path resourceFile : rootFolder) {
                if (!Files.isDirectory(resourceFile)) resources.add(resourceFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resources;
    }

    private String findProjectName(Path root) {
        try (DirectoryStream<Path> rootFolder = Files.newDirectoryStream(root)) {
            for (Path projectFolder : rootFolder) {
                if (Files.isDirectory(projectFolder)) {
                    return projectFolder.getFileName().toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
        return "";
    }
}
