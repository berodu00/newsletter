package com.kz.magazine.integration;

import com.kz.magazine.entity.Category;
import com.kz.magazine.entity.Content;
import com.kz.magazine.entity.ContentStatus;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.CategoryRepository;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser(username = "editor", roles = "USER")
class ContentScenarioTest extends BaseIntegrationTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.kz.magazine.service.ContentViewService contentViewService;

    @BeforeEach
    void setUp() {
        // Setup dependencies
        if (categoryRepository.findByCategoryName("General").isEmpty()) {
            categoryRepository.save(Category.builder().categoryName("General").displayOrder(1).build());
        }

        if (userRepository.findByUsername("editor").isEmpty()) {
            userRepository.save(User.builder().username("editor").name("Editor").role("ADMIN").build());
        }

        // Setup Content
        Category category = categoryRepository.findByCategoryName("General").orElseThrow();
        User author = userRepository.findByUsername("editor").orElseThrow();

        Content content = Content.builder()
                .title("Integration Test Content")
                .summary("Summary")
                .bodyHtml("<p>Body</p>")
                .status(ContentStatus.PUBLISHED)
                .category(category)
                .author(author)
                .publishedAt(LocalDateTime.now())
                .viewCount(0L)
                .ratingCount(0L)
                .build();

        contentRepository.save(content);
    }

    @Test
    @DisplayName("사보 목록 조회 통합 테스트")
    void getContents_Integration() throws Exception {
        mockMvc.perform(get("/api/contents")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Integration Test Content"));
    }

    @Test
    @DisplayName("사보 상세 조회 통합 테스트")
    void getContent_Integration() throws Exception {
        Content content = contentRepository.findAll().get(0);

        mockMvc.perform(get("/api/contents/" + content.getContentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test Content"));
    }
}
