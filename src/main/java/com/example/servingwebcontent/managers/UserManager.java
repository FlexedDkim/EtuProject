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

    public static List<User> getUsertype(Long id) {
        return userRepository.findByUsertype(id);
    }

    public static Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public static List<User> getUserByIdManager(Long id) {
        return userRepository.findByIdManager(id);
    }

    public static Optional<User> searchUser(String iname, String fname, String oname, Long id) {return userRepository.findAllByInameContainingIgnoreCaseAndFnameContainingIgnoreCaseAndOnameContainingIgnoreCaseAndId(iname,fname,oname,id); }
    public static List<User> searchadmin(Optional<String> iname, Optional<String> fname, Optional<String> oname, Optional<String> mail,Long usertype) {return userRepository.findAllByInameContainingIgnoreCaseAndFnameContainingIgnoreCaseAndOnameContainingIgnoreCaseAndMailContainingIgnoreCaseAndUsertype(iname, fname, oname, mail,usertype);}
}
