package com.talend.tuj.generator.elements;

import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.JobFramework;
import com.talend.tuj.generator.utils.JobType;
import com.talend.tuj.generator.utils.NodeType;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.Optional;

public interface IElement {
    boolean isOfType(NodeType type);
    Optional<String> getAttribute(String attribute);
    void replaceAttribute(String attribute, String value);
    Optional<String> getParameter(String parameter);
    void replaceParameter(String parameter, String value);
    boolean isJobOfType(JobType type);
    boolean isJobOfFramework(JobFramework type);
    Map<String, String> getAllParameters();
    void replaceParameters(Map<String, String> newParameters);
    Job getParentJob();
    Node getRawNode();
}
