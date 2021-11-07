package org.plyct.plyex;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Plyex {

    private DocGen.Options options;
    public DocGen.Options getOptions() { return options; }

    public Plyex(DocGen.Options options) {
        this.options = options;
    }

    public OpenApi augment(OpenApi openApi) throws IOException {
        new PseudoCompiler(Paths.get(this.options.getRoot()),
                this.options.getSources(), this.options.isDebug()).doCompile();

//        List<PlyMethod> plyMethods = this.findPlyMethods();
//        for (PlyMethod plyMethod : plyMethods) {
//            System.out.println("\nPLY METHOD: " + plyMethod.getMethod().getName());
//            Class clazz = plyMethod.getMethod().getDeclaringClass();
//            System.out.println("CLASS: " + clazz.getCanonicalName());
//        }

        // compiler for comments

        return openApi; // TODO clone
    }

    List<PlyMethod> findPlyMethods() {
        List<PlyMethod> plyMethods = new ArrayList<>();
        for (String pkg : this.options.getPackages()) {
            plyMethods.addAll(new Scanner(pkg).getPlyMethods());
        }
        return plyMethods;
    }

}
