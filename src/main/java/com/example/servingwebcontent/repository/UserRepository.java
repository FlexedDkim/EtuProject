package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByMail(String mail);
    Optional<User> findById(Long id);
    List<User> findByUsertype(Long usertype);
    List<User> findByIdManager(Long idManager);
    Optional<User> findAllByInameContainingIgnoreCaseAndFnameContainingIgnoreCaseAndOnameContainingIgnoreCaseAndId(String iname, String fname, String oname, Long id);
    List<User> findAllByInameContainingIgnoreCaseAndFnameContainingIgnoreCaseAndOnameContainingIgnoreCaseAndMailContainingIgnoreCaseAndUsertype(Optional<String> iname, Optional<String> fname, Optional<String> oname, Optional<String> mail,Long userType);
}