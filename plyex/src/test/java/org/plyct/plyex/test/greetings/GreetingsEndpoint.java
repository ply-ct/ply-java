package org.plyct.plyex.test.greetings;

import org.plyct.plyex.annotation.Ply;

public class GreetingsEndpoint {

    /**
     * Create a greeting
     * @param greeting
     */
    @Ply(request="create-greeting.ply.yaml#postGreeting",
         responses={"create-greeting.ply.yaml#postGreeting", "create-greeting.ply.yaml#postGreetingInvalid"})
    public void createGreeting(Greeting greeting) {
        // POST /greetings
        System.out.println("createGreeting: " + greeting);
    }

    /**
     * Retrieve a greeting
     * This greeting is very polite
     * @return greeting
     */
    @Ply(responses={"retrieve-greetings.ply.yaml#getGreeting", "retrieve-greetings.ply.yaml#getGreetingNotFound"})
    public Greeting getGreeting() {
        // GET /greetings/{name}
        return new Greeting("Hello", "World");
    }

    /**
     * List greetings. One of them may be untoward.
     */
    @Ply(responses={"retrieve-greetings.ply.yaml#getGreetings"})
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
    @Ply(request="update-greeting.ply.yaml#putGreeting",
         responses={"update-greeting.ply.yaml#putGreeting", "update-greeting.ply.yaml#putGreetingNotFound"})
    public void updateGreeting(Greeting greeting) {
        // PUT /greetings/name
        System.out.println("updateGreeting: " + greeting);
    }

    /**
     * Delete a greeting (so long)
     */
    @Ply(responses={"delete-greeting.ply.yaml#deleteGreeting", "delete-greeting.ply.yaml#deleteGreetingNotFound"})
    public void deleteGreeting() {
        // DELETE /greetings/name
        System.out.println("deleteGreeting");
    }
}
