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

    private String email;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String toString() {
        String str = salutation + ", " + name;
        if (email != null) str += " (" + email + ")";
        return str;
    }

}
