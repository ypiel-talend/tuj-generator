package com.talend.tuj.generator.utils;

public enum NodeType {
    COMPONENT("node"),
    JOBCONFIG("parameters"),
    CONTEXT("context"),
    CONNECTION("connection"),
    SUBJOB("subjob"),
    TPProperty("TalendProperties:Property"),
    TPProcessItem("TalendProperties:ProcessItem"),
    TPItemState("TalendProperties:ItemState");

    private String xmlNodeName;

    NodeType(String xmlNodeName) {
        this.xmlNodeName = xmlNodeName;
    }

    public String getXmlNodeName() {
        return xmlNodeName;
    }
}
