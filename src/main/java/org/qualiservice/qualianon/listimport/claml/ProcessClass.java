package org.qualiservice.qualianon.listimport.claml;


public class ProcessClass {

    private final ProcessClass superClass;
    private final String text;

    public ProcessClass(ProcessClass superClass, String text) {
        this.superClass = superClass;
        this.text = text;
    }

    public ProcessClass getSuperClass() {
        return superClass;
    }

    public String getText() {
        return text;
    }

}
