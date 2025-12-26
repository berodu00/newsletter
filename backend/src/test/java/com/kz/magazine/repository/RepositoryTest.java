package com.kz.magazine.repository;

import com.kz.magazine.entity.Category;
import com.kz.magazine.entity.Content;
import com.kz.magazine.entity.User;
import com.kz.magazine.entity.ContentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import com.kz.magazine.config.JpaConfig;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
class RepositoryTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Content Repository Mapping Test")
    void testContentMapping() {
        // 1. Verify standard categories exist (from migration)
        Category category = categoryRepository.findByCategoryName("Special")
                .orElseThrow(() -> new IllegalStateException("Migration failed: Special category missing"));

        // 2. Verify admin user exists (from migration)
        User author = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("Migration failed: admin user missing"));

        // 3. Save new content
        Content content = new Content();
        content.setTitle("Test Title");
        content.setSummary("Test Summary");
        content.setBodyHtml("<p>Test Body</p>");
        content.setCategory(category);
        content.setAuthor(author);
        content.setStatus(ContentStatus.DRAFT);

        Content savedContent = contentRepository.save(content);

        // 4. Verify save
        assertThat(savedContent.getContentId()).isNotNull();
        assertThat(savedContent.getTitle()).isEqualTo("Test Title");
        assertThat(savedContent.getCategory().getCategoryName()).isEqualTo("Special");
        assertThat(savedContent.getAuthor().getUsername()).isEqualTo("admin");
    }
}
