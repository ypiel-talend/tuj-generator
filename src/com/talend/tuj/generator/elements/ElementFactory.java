package com.talend.tuj.generator.elements;

import com.talend.tuj.generator.utils.Job;
import org.w3c.dom.Node;

import java.util.Optional;

public class ElementFactory {
    private static Optional<ElementFactory> instance = Optional.empty();

    private ElementFactory() {}

    public static ElementFactory getInstance(){
        if(!instance.isPresent()){
            instance = Optional.of(new ElementFactory());
        }
        return instance.get();
    }

    public IElement createElement(Node node, Job job){
        return new StudioElement(node, job);
    }
}
