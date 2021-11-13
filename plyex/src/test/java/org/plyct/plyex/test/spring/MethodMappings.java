package org.plyct.plyex.test.spring;

import org.springframework.web.bind.annotation.*;

@RequestMapping(path = {"/heres"}, method = RequestMethod.DELETE)
public class MethodMappings {

    @GetMapping
    public Something get() {
        return new Something("irrelevant");
    }

    @PostMapping("something")
    public void post(Something something) {
        System.out.println("POSTed something " + something.getStatus());
    }

    @PutMapping("/somethingElse")
    public void put(Something something) {
        System.out.println("PUT something " + something.getStatus());
    }

    @RequestMapping(path = "somethingElseAgain", method = RequestMethod.PATCH)
    public void patch(Something something) {
        System.out.println("PATCHed something " + something.getStatus());
    }

}
