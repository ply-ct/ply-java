package org.plyct.plyex.test.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class NoClassMapping {

    @RequestMapping(method = RequestMethod.GET, path = "/something")
    public Something getOne() {
        return new Something("silly");
    }

    @GetMapping(path = { "anotherThing", "yetAnotherThing" })
    public Something getAnother() { return new Something("otherwise"); }
}
