package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.elements.IElement;
import com.talend.tuj.generator.utils.NodeType;

import java.util.Map;
import java.util.stream.Collectors;

public class ContextSubstitutionProcessor implements IProcessor {

    private Map<String, String> substitutions;

    public ContextSubstitutionProcessor(Map<String, String> substitutions) {
        this.substitutions = substitutions;
    }

    @Override
    public boolean shouldBeProcessed(IElement component) {
        return component.isOfType(NodeType.CONTEXT);
    }

    @Override
    public void process(IElement component) {
        component.replaceParameters(
            component.getAllParameters().entrySet().stream().filter(
                parameter -> substitutions.keySet().stream().anyMatch(parameter.getValue()::contains)
            ).peek(
                parameter -> {
                    String newParameter = parameter.getValue();
                    for(Map.Entry<String, String> entry : substitutions.entrySet()){
                        newParameter = newParameter.replaceAll(entry.getKey(), entry.getValue());
                    }
                    parameter.setValue(newParameter);
                }
            ).collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
            )
        );
    }
}
