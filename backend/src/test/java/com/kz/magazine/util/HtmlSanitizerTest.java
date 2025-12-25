package com.kz.magazine.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlSanitizerTest {

    @Test
    @DisplayName("Should remove script tags")
    void shouldRemoveScriptTags() {
        String input = "<p>Hello <script>alert('XSS')</script> World</p>";
        String expected = "<p>Hello  World</p>";
        String actual = HtmlSanitizerUtil.sanitize(input);

        // Assertj contains normalization if needed, but simple equals works for this
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should allow safe tags and attributes")
    void shouldAllowSafeTags() {
        String input = "<div class=\"content\"><h1>Title</h1><p><b>Bold</b> and <i>Italic</i></p></div>";
        // The sanitizer might strip unknown classes or rearrange attributes.
        // Our policy doesn't explicitly allow 'class' attribute on div.
        // Let's check what our policy actually defined: .allowElements("div") but NOT
        // .allowAttributes("class").onElements("div")
        // So class should be stripped.
        String expected = "<div><h1>Title</h1><p><b>Bold</b> and <i>Italic</i></p></div>";
        String actual = HtmlSanitizerUtil.sanitize(input);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should allow valid links but enforce nofollow")
    void shouldAllowLinksWithNofollow() {
        String input = "<a href=\"https://example.com\">Link</a>";
        String expected = "<a href=\"https://example.com\" rel=\"nofollow\">Link</a>";
        String actual = HtmlSanitizerUtil.sanitize(input);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should allow Youtube iframe")
    void shouldAllowYoutubeIframe() {
        String input = "<iframe src=\"https://www.youtube.com/embed/12345\" width=\"560\" height=\"315\"></iframe>";
        String actual = HtmlSanitizerUtil.sanitize(input);

        assertThat(actual).contains("<iframe");
        assertThat(actual).contains("src=\"https://www.youtube.com/embed/12345\"");
    }
}
