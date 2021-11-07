package org.plyct.plyex.test.greetings;

public class Greeting {

    Greeting(String salutation, String name) {
        this.salutation = salutation;
        this.name = name;
    }

    private String salutation;
    public String getSalutation() { return salutation; }
    public void setSalutation(String salutation) { this.salutation = salutation; }

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String toString() {
        return salutation + ", " + name;
    }

}
