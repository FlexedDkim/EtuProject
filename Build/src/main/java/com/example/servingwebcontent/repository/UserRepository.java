package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByMail(String mail);
    /*  public static final Map<Integer, User> CLIENT_REPOSITORY_MAP = new HashMap<>();

    public static final AtomicInteger CLIENT_ID_HOLDER = new AtomicInteger();

    @Override
    public void create(User user) {
        final int userId = CLIENT_ID_HOLDER.incrementAndGet();
        user.setId(userId);
        CLIENT_REPOSITORY_MAP.put(userId, user);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(CLIENT_REPOSITORY_MAP.values());
    }
/*
    @Override
    public User read(int id) {
        return CLIENT_REPOSITORY_MAP.get(id);
    }

    @Override
    public boolean update(User user, int id) {
        if (CLIENT_REPOSITORY_MAP.containsKey(id)) {
            user.setId(id);
            CLIENT_REPOSITORY_MAP.put(id, user);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Long id) {
        return CLIENT_REPOSITORY_MAP.remove(id) != null;
    } */
}