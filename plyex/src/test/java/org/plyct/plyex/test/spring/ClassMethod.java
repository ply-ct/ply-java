package org.plyct.plyex.test.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(method=RequestMethod.GET)
public class ClassMethod {

    @RequestMapping(value="/something")
    public Something get() {
        return new Something("important");
    }
}
