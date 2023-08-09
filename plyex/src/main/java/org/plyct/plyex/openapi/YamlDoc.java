package org.plyct.plyex.openapi;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class YamlDoc implements ApiDoc {

    private final Yaml yaml;

    private final Constructor constructor;
    private final Representer representer;
    private final DumperOptions dumperOptions;

    public YamlDoc() {
        this.constructor = new Constructor(OpenApi.class, new LoaderOptions());

        this.dumperOptions = new DumperOptions();
        this.dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.dumperOptions.setPrettyFlow(true);
        this.dumperOptions.setSplitLines(false);
        this.dumperOptions.setIndentWithIndicator(true);

        this.representer = new Representer(this.dumperOptions);

        this.addCustomTypeDescription(OpenApi.BodyContent.class);
        this.addCustomTypeDescription(OpenApi.Schema.class);
        this.addCustomTypeDescription(OpenApi.Items.class);
        this.addCustomTypeDescription(OpenApi.Operation.class);

        this.yaml = new Yaml(this.constructor, this.representer, this.dumperOptions);
    }

    private void addCustomTypeDescription(Class<? extends Object> clazz) {
        TypeDescription typeDescription = new TypeDescription(clazz);
        typeDescription.substituteProperty("application/json", clazz, "getApplicationJson", "setApplicationJson");
        typeDescription.substituteProperty("$ref", clazz, "getRef", "setRef");
        typeDescription.substituteProperty("x-codeSamples", clazz, "getCodeSamples", "setCodeSamples");
        typeDescription.setExcludes("applicationJson", "ref", "codeSamples");
        constructor.addTypeDescription(typeDescription);
        representer.addTypeDescription(typeDescription);
    }

    public OpenApi load(String yaml) {
        return this.yaml.load(yaml);
    }

    public String dump(OpenApi openApi, int indent) {
        this.dumperOptions.setIndent(indent);
        this.dumperOptions.setIndicatorIndent(indent);
        String yaml = this.yaml.dump(openApi);
        List<String> lines = new ArrayList<>();
        for (String line : yaml.split("\\r?\\n")) {
            int bangBang = line.indexOf("!!");
            if (bangBang == -1) {
                lines.add(line);
            } else if (bangBang > 0) {
                lines.add(line.substring(0, bangBang).replaceAll("\\s+$", ""));
            }
        }
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }

    class Representer extends org.yaml.snakeyaml.representer.Representer {
        public Representer(DumperOptions dumperOptions) {
            super(dumperOptions);
            setPropertyUtils(new PropertyUtils() {
                @Override
                protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
                    return getPropertiesMap(type, bAccess).values().stream().sequential()
                            .filter(prop -> prop.isReadable() && (isAllowReadOnlyProperties() || prop.isWritable()))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                }
            });
        }

        @Override
        protected Set<Property> getProperties(Class<? extends Object> type) {
            Set<Property> propSet = super.getProperties(type);
            // code samples go at the end
            if (type == OpenApi.Operation.class) {
                Set<Property> opPropSet = new LinkedHashSet<>();
                Property codeSamplesProp = null;
                for (Property prop : propSet) {
                    if (prop.getName().equals("x-codeSamples")) {
                        codeSamplesProp = prop;
                    } else {
                        opPropSet.add(prop);
                    }
                }
                if (codeSamplesProp != null) opPropSet.add(codeSamplesProp);
                return opPropSet;
            } else {
                return propSet;
            }
        }

        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            // ignore nulls
            if (propertyValue == null) {
                return null;
            } else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }

    }

    class TypeDescription extends org.yaml.snakeyaml.TypeDescription {
        public TypeDescription(Class<? extends Object> clazz) {
            super(clazz);
        }

        @Override
        public Property getProperty(String name) {
            if (name.equals("application/json")) {
                return super.getProperty("applicationJson");
            } else if (name.equals("$ref")) {
                return super.getProperty("ref");
            } else if (name.equals("x-codeSamples")) {
                return super.getProperty("codeSamples");
            }
            return super.getProperty(name);
        }
    }
}
