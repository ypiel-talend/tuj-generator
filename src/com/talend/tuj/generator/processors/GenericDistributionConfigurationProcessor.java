package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.elements.IElement;
import com.talend.tuj.generator.utils.JobFramework;
import com.talend.tuj.generator.utils.JobType;
import com.talend.tuj.generator.utils.NodeType;

public class GenericDistributionConfigurationProcessor implements IProcessor {
    private String distribution_name;
    private String distribution_version;

    public GenericDistributionConfigurationProcessor(String distribution_name, String distribution_version) {
        this.distribution_version = distribution_version;
        this.distribution_name = distribution_name;
    }

    @Override
    public boolean shouldBeProcessed(IElement component) {
        return !component.isJobOfType(JobType.STANDARD) && component.isOfType(NodeType.JOBCONFIG);
    }

    @Override
    public void process(IElement component) {
        component.replaceParameter("DISTRIBUTION", distribution_name);

        if (component.isJobOfFramework(JobFramework.MAPREDUCE))
            component.replaceParameter("MR_VERSION", distribution_version);
        else component.replaceParameter("SPARK_VERSION", distribution_version);
    }
}
