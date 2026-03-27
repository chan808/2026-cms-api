package com.malgn.content.dto;

import java.time.LocalDateTime;

import com.malgn.content.Content;

public record ContentResponse(
        Long id,
        String title,
        String description,
        Long viewCount,
        String createdBy,
        String lastModifiedBy,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    public static ContentResponse from(Content content) {
        return new ContentResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getViewCount(),
                content.getCreatedBy(),
                content.getLastModifiedBy(),
                content.getCreatedDate(),
                content.getLastModifiedDate()
        );
    }
}
