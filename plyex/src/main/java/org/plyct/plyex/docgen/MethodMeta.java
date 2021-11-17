package org.plyct.plyex.docgen;

import org.plyct.plyex.PlyMethod;

import java.util.ArrayList;
import java.util.List;

public class MethodMeta {

    private String summary;
    public String getSummary() { return this.summary; }

    private String description;
    public String getDescription() { return this.description; }

    public MethodMeta(PlyMethod plyMethod) {
        if (plyMethod.getDocComment() != null) {
            String comment = plyMethod.getDocComment().trim();
            if (!comment.isEmpty()) {
                List<String> lines = new ArrayList<>();
                for (String line : comment.split("\\r?\\n")) {
                    String l = line.trim();
                    if (l.startsWith("@")) break;
                    lines.add(l);
                }
                if (!lines.isEmpty()) {
                    String summary = lines.get(0);
                    String description = "";
                    int dot = lines.get(0).indexOf('.');
                    if (dot > 0 && dot + 1 < lines.get(0).length()) {
                        summary = lines.get(0).substring(0, dot + 1);
                        description = lines.get(0).substring(dot + 1).trim();
                    } else {
                        for (int i = 1; i < lines.size(); i++) {
                            if (description.length() > 0) description += System.lineSeparator();
                            description += lines.get(i);
                        }
                    }
                    if (summary.endsWith(".")) summary = summary.substring(0, summary.length() - 1);
                    this.summary = summary;
                    if (description.length() > 0) this.description = description;
                }
            }
        }
    }
}
