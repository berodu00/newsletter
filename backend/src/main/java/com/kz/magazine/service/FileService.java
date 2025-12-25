package com.kz.magazine.service;

import com.kz.magazine.dto.file.FileResponseDto;
import com.kz.magazine.entity.ResourceFile;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.ResourceFileRepository;
import com.kz.magazine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final ResourceFileRepository resourceFileRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public FileResponseDto uploadFile(MultipartFile file, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }

        try {
            // 1. Prepare Storage Path (YYYY/MM)
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            Path uploadPath = Paths.get(uploadDir).resolve(datePath);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generate Unique Filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;
            Path destination = uploadPath.resolve(storedFilename);

            // 3. Save File
            file.transferTo(destination);

            // 4. Save Metadata
            ResourceFile resourceFile = ResourceFile.builder()
                    .originalName(originalFilename)
                    .storedName(datePath + "/" + storedFilename) // Store relative path
                    .filePath(destination.toString())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .sha256("SHA-256-PLACEHOLDER") // TODO: Implement SHA256 if needed
                    .uploadedBy(user.getUserId())
                    .build();

            ResourceFile savedFile = resourceFileRepository.save(resourceFile);

            // 5. Return DTO
            // URL strategy: /uploads/{storedName} usually handled by ResourceHandlerconfig
            // For now, return path as URL placeholder
            return FileResponseDto.from(savedFile, "");

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
