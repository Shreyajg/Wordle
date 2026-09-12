package com.example.guessgame.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "users")

public class User {
    
    @Id
    private String id;
    private String username;
    private String passwordHash;
    private Role role;

    public User()
    {
    }

    public User(String username,String passwordHash,Role role)
    {
        this.username=username;
        this.passwordHash=passwordHash;
        this.role=role;
    }

    public String getPasswordHash()
    {
        return this.passwordHash;
    }

    public Role getRole()
    {
        return this.role;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getId()
    {
        return this.id;
    }
}
