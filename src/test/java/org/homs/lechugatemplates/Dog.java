package org.homs.lechugatemplates;

public class Dog {

    public final long id;
    public final String name;

    public Dog(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}