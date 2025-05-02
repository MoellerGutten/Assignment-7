package com.example.accessing_data_rest.services;

import com.example.accessing_data_rest.model.Player;
import com.example.accessing_data_rest.model.User;
import com.example.accessing_data_rest.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        return result;
    }
    public List<User> searchUsers(String name) {
        List<User> user = userRepository.findByName(name);
        return user;
    }
    @Transactional
    public User createUser(User user) {
        User result = userRepository.save(user);
        return result;
    }
}
