package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long>  {
    List<File> findAllByIdCard(Long idcard);

    Optional<File> findAllById(Long id);
}