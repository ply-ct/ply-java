package org.plyct.plyex;

import java.util.Map;

public class MethodMeta {

    public class Examples {
        private String request;
        public String getRequest() { return this.request; }

        private Map<Integer,String> responses;
        public Map<Integer,String> getResponses() { return this.responses; }
    }

    private String summary;
    public String getSummary() { return this.summary; }
    void setSummary(String summary) { this.summary = summary; }

    private String description;
    public String getDescription() { return this.description; }
    void setDescription(String description) { this.description = description; }

    private Examples examples;
    public Examples getExamples() { return this.examples; }
    void setExamples(Examples examples) { this.examples = examples; }
}
