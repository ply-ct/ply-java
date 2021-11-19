package org.plyct.plyex.code;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.plyct.plyex.Endpoint;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodeSamples {

    public static final String[] LANGUAGES = { "Java", "Python", "TypeScript" };
    private final Map<String, Template> templates = new LinkedHashMap<>();

    public CodeSamples(Endpoint.Method method) throws IOException {
        if (method == Endpoint.Method.get || method == Endpoint.Method.post || method == Endpoint.Method.put) {
            readTemplates(method);
        }
    }

    public void readTemplates(Endpoint.Method method) throws IOException {
        TemplateLoader loader = new ClassPathTemplateLoader("/templates/" + method, ".handlebars");
        Handlebars handlebars = new Handlebars(loader);
        handlebars.prettyPrint(true);
        handlebars.registerHelper("capitalize", (String lower, Options options) -> {
            if (lower != null && !lower.isEmpty()) {
                return String.valueOf(lower.charAt(0)).toUpperCase() + lower.substring(1);
            } else {
                return lower;
            }
        });
        handlebars.registerHelper("expression", (String str, Options options) -> {
            return "{" + str + "}";
        });

        for (String language : LANGUAGES) {
            Template template = handlebars.compile(language);
            this.templates.put(language, template);
        }
    }

    public Map<String,String> getSamples(TemplateContext context) throws IOException {
        Map<String,String> samples = new LinkedHashMap<>();
        for (String lang : this.templates.keySet()) {
            Template template = this.templates.get(lang);
            samples.put(lang, template.apply(context));
        }
        return samples;
    }
}
