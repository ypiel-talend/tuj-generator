package com.talend.tuj.generator.elements;

import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.JobFramework;
import com.talend.tuj.generator.utils.JobType;
import com.talend.tuj.generator.utils.NodeType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractElement implements IElement {
    protected Node xmlNode;
    protected Job job;

    public AbstractElement(Node node, Job job) {
        this.xmlNode = node;
        this.job = job;
    }

    @Override
    public boolean isOfType(NodeType type) {
        return xmlNode.getNodeName().equals(type.getXmlNodeName());
    }

    @Override
    public Optional<String> getAttribute(String attribute) {
        try {
            return Optional.of(xmlNode.getAttributes().getNamedItem(attribute).getNodeValue());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public void replaceAttribute(String attribute, String value) {
        try {
            xmlNode.getAttributes().getNamedItem(attribute).setNodeValue(value);
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public Optional<String> getParameter(String parameter) {
        if (xmlNode.hasChildNodes()) {
            NodeList childs = xmlNode.getChildNodes();
            for (int nodeIndex = 0; nodeIndex < childs.getLength(); nodeIndex++) {
                Node childNode = childs.item(nodeIndex);
                try {
                    if (childNode.getAttributes().getNamedItem("name").getNodeValue().equals(parameter)) {
                        return Optional.of(childNode.getAttributes().getNamedItem("value").getNodeValue());
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void replaceParameter(String parameter, String value) {
        if (xmlNode.hasChildNodes()) {
            NodeList childs = xmlNode.getChildNodes();
            for (int nodeIndex = 0; nodeIndex < childs.getLength(); nodeIndex++) {
                Node childNode = childs.item(nodeIndex);
                try {
                    if (childNode.getAttributes().getNamedItem("name").getNodeValue().equals(parameter)) {
                        childNode.getAttributes().getNamedItem("value").setNodeValue(value);
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
    }

    @Override
    public boolean isJobOfType(JobType type) {
        return job.getType().equals(type);
    }

    @Override
    public boolean isJobOfFramework(JobFramework type) {
        return job.getFramework().equals(type);
    }

    @Override
    public Map<String, String> getAllParameters() {
        Map<String, String> parameters = new HashMap<>();

        NodeList childs = xmlNode.getChildNodes();
        for (int nodeIndex = 0; nodeIndex < childs.getLength(); nodeIndex++) {
            NamedNodeMap childAttributes = childs.item(nodeIndex).getAttributes();
            parameters.put(childAttributes.getNamedItem("name").getNodeValue(), childAttributes.getNamedItem("value").getNodeValue());
        }

        return parameters;
    }

    @Override
    public void replaceParameters(Map<String, String> newParameters) {
        NodeList childs = xmlNode.getChildNodes();
        for (int nodeIndex = 0; nodeIndex < childs.getLength(); nodeIndex++) {
            NamedNodeMap childAttributes = childs.item(nodeIndex).getAttributes();
            String attributeName = childAttributes.getNamedItem("name").getNodeValue();
            if (newParameters.containsKey(attributeName)) {
                replaceParameter(attributeName, newParameters.get(attributeName));
            }
        }
    }

    @Override
    public Job getParentJob() {
        return job;
    }

    @Override
    public Node getRawNode() {
        return xmlNode;
    }
}
