package org.plyct.plyex.test.greetings;

import org.plyct.plyex.annotation.Ply;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Null;

public class GreetingsEndpoint {

    /**
     * Create a greeting
     * @param greeting
     */
    @Ply(request="src/test/ply/create-greeting.ply.yaml#postGreeting",
         responses={"src/test/ply/create-greeting.ply.yaml#postGreeting",
                 "src/test/ply/create-greeting.ply.yaml#postGreetingInvalid"})
    public void createGreeting(Greeting greeting) {
        // POST /greetings
        System.out.println("createGreeting: " + greeting);
    }

    /**
     * Retrieve a greeting
     * This greeting is very polite
     * Valid annotations are retained, so test signature matching
     * @return greeting
     */
    @Ply(responses={"src/test/ply/retrieve-greetings.ply.yaml#getGreeting",
            "src/test/ply/retrieve-greetings.ply.yaml#getGreetingNotFound"})
    public Greeting getGreeting(
            @Valid @Digits(integer = 0, fraction = 0) @RequestHeader(value = "x-header", required = false) String header,
            @Valid @RequestParam(value = "param", required = false) String param) {
        // GET /greetings/{name}
        return new Greeting("Hello", "World");
    }

    /**
     * List greetings. One of them may be untoward.
     */
    @Ply(responses={"src/test/ply/retrieve-greetings.ply.yaml#getGreetings"})
    public Greeting[] getGreetings() {
        // GET /greetings
        return new Greeting[] {
            new Greeting("Hello", "World"),
            new Greeting("Goodbye", "Cruel World")
        };
    }

    /**
     * Update a greeting.
     * Replaces entire greeting contents
     * @param greeting
     */
    @Ply(request="src/test/ply/update-greeting.yaml#putGreeting",
         responses={"src/test/ply/update-greeting.yaml#putGreeting",
                 "src/test/ply/update-greeting.yaml#putGreetingNotFound"})
    public void updateGreeting(Greeting greeting) {
        // PUT /greetings/name
        System.out.println("updateGreeting: " + greeting);
    }

    /**
     * Delete a greeting (so long)
     */
    @Ply(responses={"src/test/ply/delete-greeting.ply.yaml#deleteGreeting",
            "src/test/ply/delete-greeting.ply.yaml#deleteGreetingNotFound"})
    public void deleteGreeting() {
        // DELETE /greetings/name
        System.out.println("deleteGreeting");
    }
}
