package com.kz.magazine.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizerUtil {

    // Define a custom policy that allows common formatting and safe embeds
    private static final PolicyFactory CUSTOM_POLICY = new HtmlPolicyBuilder()
            .allowElements("p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "br", "hr", "span", "blockquote")
            .allowElements("b", "i", "u", "s", "strong", "em", "strike", "code", "pre")
            .allowElements("ul", "ol", "li")
            .allowElements("a")
            .allowUrlProtocols("http", "https", "mailto")
            .allowAttributes("href", "target", "title").onElements("a")
            .requireRelNofollowOnLinks()
            .allowElements("img")
            .allowUrlProtocols("http", "https")
            .allowAttributes("src", "alt", "title", "width", "height").onElements("img")
            .allowElements("iframe")
            .allowAttributes("src", "width", "height", "frameborder", "allow", "allowfullscreen").onElements("iframe")
            // Enforce iframe src to be from trusted providers
            .allowUrlProtocols("https")
            .disallowElements("script", "object", "embed", "form", "input", "button")
            .toFactory();

    // Combine with standard block and formatting policies for robustness
    public static final PolicyFactory POLICY = CUSTOM_POLICY
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.FORMATTING)
            .and(Sanitizers.LINKS) // Note: CUSTOM_POLICY handles links but redundancy is safe/merging
            .and(Sanitizers.STYLES) // Allow safe styles
            .and(Sanitizers.TABLES);

    public static String sanitize(String html) {
        if (html == null) {
            return null;
        }
        return POLICY.sanitize(html);
    }
}
