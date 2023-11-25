package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.Comment;
import com.example.servingwebcontent.database.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>  {
    long countByIdCard(Long idcard);

    List<Comment> findAllByIdCard(Long idcard);

}