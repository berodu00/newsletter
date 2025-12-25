package com.kz.magazine.dto.file;

import com.kz.magazine.entity.ResourceFile;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponseDto {
    private Long fileId;
    private String originalName;
    private String storedName;
    private String url;
    private String mimeType;
    private Long fileSize;

    public static FileResponseDto from(ResourceFile file, String baseUrl) {
        return FileResponseDto.builder()
                .fileId(file.getFileId())
                .originalName(file.getOriginalName())
                .storedName(file.getStoredName())
                .url(baseUrl + "/uploads/" + file.getStoredName()) // Simplified URL strategy for now
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .build();
    }
}
