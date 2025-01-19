package com.example.app.service;

import com.example.app.model.Role;
import com.example.app.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List<User> users;

    public UserService() {
        users = FileStorage.loadUsers();
    }

    public User login(String login, String password) throws Exception {
        for (User u : users) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                return u;
            }
        }
        throw new Exception("Invalid login or password!");
    }

    public User register(String login, String password, Role role) throws Exception {
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                throw new Exception("Such login already exists!");
            }
        }
        User newUser = new User(login, password, role);
        users.add(newUser);
        FileStorage.saveUsers(users);
        return newUser;
    }
}

