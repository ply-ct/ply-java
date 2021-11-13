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

    private String description;
    public String getDescription() { return this.description; }

    private Examples examples;
    public Examples getExamples() { return this.examples; }
}
