package com.example.servingwebcontent.managers;

import com.example.servingwebcontent.database.Comment;
import com.example.servingwebcontent.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentManager {
    @Autowired
    private static CommentRepository commentRepository;

    public CommentManager(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public static Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public static Long countByIdCard(Long idown) {
        return commentRepository.countByIdCard(idown);
    }

    public static List<Comment> readAllByIdCard(Long idcard) {
        return commentRepository.findAllByIdCard(idcard);
    }
}
