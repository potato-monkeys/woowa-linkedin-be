package com.example.hackethon.storage;

import com.example.hackethon.common.exception.BusinessException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file, String subdirectory) {
        try {
            Path dir = Paths.get(uploadDir, subdirectory);
            Files.createDirectories(dir);

            String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            file.transferTo(dir.resolve(filename));

            return "/files/" + subdirectory + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException("파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
