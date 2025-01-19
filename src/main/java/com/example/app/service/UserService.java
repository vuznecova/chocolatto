package com.example.app.service;

import com.example.app.model.Role;
import com.example.app.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List<User> users;

    public UserService() {
        // Загружаем пользователей из файла при инициализации
        users = FileStorage.loadUsers();
    }

    public User login(String login, String password) throws Exception {
        // Ищем пользователя по логину и паролю
        for (User u : users) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                return u;
            }
        }
        // Если не нашли — выбрасываем исключение
        throw new Exception("Неверный логин или пароль!");
    }

    public User register(String login, String password, Role role) throws Exception {
        // Проверка: нет ли уже такого логина
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                throw new Exception("Логин уже существует!");
            }
        }
        User newUser = new User(login, password, role);
        users.add(newUser);
        FileStorage.saveUsers(users);
        return newUser;
    }
}

