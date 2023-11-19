package com.example.servingwebcontent.managers;

import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserManager {
    @Autowired
    private static UserRepository userRepository;

    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User>  readAll() {
        return userRepository.findAll();
    }

    public static Optional<User> getUserByMail(String mail) {
        return userRepository.findByMail(mail);
    }
}
