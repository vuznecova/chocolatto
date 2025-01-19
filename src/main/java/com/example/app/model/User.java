package com.example.app.model;

public class User {
    private String login;
    private String password;
    private Role role;

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return login + ";" + password + ";" + role;
    }

    public static User fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length < 3) return null;
        Role parsedRole = Role.valueOf(parts[2]);
        return new User(parts[0], parts[1], parsedRole);
    }
}
