package com.kz.magazine.repository;

import com.kz.magazine.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {
    Page<Content> searchContents(String keyword, String status, Pageable pageable);
}
