package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.elements.IElement;
import com.talend.tuj.generator.utils.Job;
import com.talend.tuj.generator.utils.NodeType;
import com.talend.tuj.generator.utils.TUJ;
import org.w3c.dom.Node;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class FileNameSubstitutionProcessor implements IProcessor {

    private Map<String, String> substitutions;

    public FileNameSubstitutionProcessor(Map<String, String> substitutions) {
        this.substitutions = substitutions;
    }

    @Override
    public boolean shouldBeProcessed(IElement component) {
        return component.isOfType(NodeType.TPProperty) || component.isOfType(NodeType.TPProcessItem) || component.isOfType(NodeType.TPItemState) ||
                (component.isOfType(NodeType.COMPONENT) && component.getAttribute("componentName").orElse("").equals("tRunJob"));
    }

    @Override
    public void process(IElement component) {
        if (component.isOfType(NodeType.TPProperty)) {
            Job parentJob = component.getParentJob();

            parentJob.setName(applySubstitutions(parentJob.getName()));
            if (parentJob.getFsPath().isPresent()) {
                parentJob.setFsPath(Optional.of(Paths.get(applySubstitutions(parentJob.getFsPath().get().toString()))));
            }

            if (parentJob.getTuj().isPresent()) {
                TUJ parentTUJ = parentJob.getTuj().get();
                parentTUJ.setName(applySubstitutions(parentTUJ.getName()));
                parentTUJ.setProjectName(applySubstitutions(parentTUJ.getProjectName()));
            }

            component.replaceAttribute("label", applySubstitutions(component.getAttribute("label").orElse("")));
            component.replaceAttribute("displayName", applySubstitutions(component.getAttribute("displayName").orElse("")));

        } else if (component.isOfType(NodeType.TPItemState)) {
            component.replaceAttribute("path", applySubstitutions(component.getAttribute("path").orElse("")));
        } else if (component.isOfType(NodeType.TPProcessItem)) {
            Node node = component.getRawNode().getChildNodes().item(1).getAttributes().getNamedItem("href");
            node.setNodeValue(applySubstitutions(node.getNodeValue()));
        } else if (component.isOfType(NodeType.COMPONENT)) {
            component.replaceAttribute("PROCESS", applySubstitutions(component.getAttribute("PROCESS").orElse("")));
        }

    }

    private String applySubstitutions(String str) {
        String newStr = str;
        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            newStr = newStr.replaceAll(entry.getKey(), entry.getValue());
        }
        return newStr;
    }
}
