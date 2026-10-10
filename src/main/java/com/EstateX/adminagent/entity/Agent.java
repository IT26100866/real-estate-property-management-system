package com.EstateX.adminagent.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("AGENT")
public class Agent extends Person {

    protected Agent() { }

    public Agent(String username, String fullName, String email, String phone, String address,
                 String password, AccountStatus status) {
        super(username, fullName, email, phone, address, password, status);
    }

    @Override
    public String getType() { return "Agent"; }
}
