package org.plyct.plyex.test.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping({"/heres", "/theres"})
public class MultiplePaths {

    @RequestMapping(method = RequestMethod.GET, path = "something")
    public Something get() {
        return new Something("irrelevant");
    }

    @RequestMapping(method = RequestMethod.POST, value = {"anything","nothing"})
    public void post(Something something) {
        System.out.println("POSTed something " + something.getStatus());
    }
}
