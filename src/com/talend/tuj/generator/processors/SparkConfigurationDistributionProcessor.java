package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.elements.IElement;
import com.talend.tuj.generator.utils.JobFramework;
import com.talend.tuj.generator.utils.NodeType;

public class SparkConfigurationDistributionProcessor implements IProcessor {
    private String distribution_name, distribution_version;

    SparkConfigurationDistributionProcessor(String distribution_name, String distribution_version){
        this.distribution_name = distribution_name;
        this.distribution_version = distribution_version;
    }

    @Override
    public boolean shouldBeProcessed(IElement component) {
        return (component.isJobOfFramework(JobFramework.SPARK) || component.isJobOfFramework(JobFramework.SPARK_STREAMING)) && component.isOfType(NodeType.JOBCONFIG);
    }

    @Override
    public void process(IElement component) {
        try{
            component.replaceParameter("SPARK_LOCAL_MODE", "false");
            component.replaceParameter("DISTRIBUTION", distribution_name);
            component.replaceParameter("SPARK_VERSION", distribution_version);
        }catch(NullPointerException ignored){}
    }
}
