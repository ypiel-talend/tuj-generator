package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.elements.IElement;

public interface IProcessor {
    boolean shouldBeProcessed(IElement component);
    void process(IElement component);
}
