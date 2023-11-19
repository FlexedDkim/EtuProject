package com.example.servingwebcontent.managers;

import com.example.servingwebcontent.database.File;
import com.example.servingwebcontent.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileManager {
    @Autowired
    private static FileRepository fileRepository;

    public FileManager(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public static File createFile(File file) {
        return fileRepository.save(file);
    }
}
