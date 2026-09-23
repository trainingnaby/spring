package fr.formation.springboot.greeting.autoconfigure;

public class GreetingService {

    private final String prefix;

    public GreetingService(String prefix) {
        this.prefix = prefix;
    }

    public String greet(String name) {
        return prefix + " " + name + " !";
    }
}
