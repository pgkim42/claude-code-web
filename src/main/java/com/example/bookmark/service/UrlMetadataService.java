package com.example.bookmark.service;

import com.example.bookmark.dto.UrlMetadata;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
public class UrlMetadataService {

    private static final int TIMEOUT_MS = 10000; // 10 seconds
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    /**
     * Fetch metadata from a given URL
     */
    public UrlMetadata fetchMetadata(String url) {
        try {
            log.info("Fetching metadata from URL: {}", url);

            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();

            UrlMetadata metadata = UrlMetadata.builder()
                    .url(url)
                    .build();

            // Extract title (prefer Open Graph, fallback to <title>)
            metadata.setTitle(extractTitle(doc));

            // Extract description (prefer Open Graph, fallback to meta description)
            metadata.setDescription(extractDescription(doc));

            // Extract thumbnail/image (Open Graph image)
            metadata.setThumbnailUrl(extractThumbnail(doc, url));

            // Extract favicon
            metadata.setFaviconUrl(extractFavicon(doc, url));

            // Extract site name
            metadata.setSiteName(extractSiteName(doc, url));

            // Extract author
            metadata.setAuthor(extractAuthor(doc));

            // Extract published date
            metadata.setPublishedDate(extractPublishedDate(doc));

            log.info("Successfully fetched metadata for: {}", url);
            return metadata;

        } catch (Exception e) {
            log.error("Failed to fetch metadata from URL: {}", url, e);
            // Return minimal metadata with just the URL
            return UrlMetadata.builder()
                    .url(url)
                    .title(url) // Use URL as fallback title
                    .build();
        }
    }

    private String extractTitle(Document doc) {
        // Try Open Graph title first
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        if (ogTitle != null && ogTitle.hasAttr("content")) {
            return ogTitle.attr("content");
        }

        // Try Twitter title
        Element twitterTitle = doc.selectFirst("meta[name=twitter:title]");
        if (twitterTitle != null && twitterTitle.hasAttr("content")) {
            return twitterTitle.attr("content");
        }

        // Fallback to <title> tag
        Element titleElement = doc.selectFirst("title");
        if (titleElement != null) {
            return titleElement.text();
        }

        return null;
    }

    private String extractDescription(Document doc) {
        // Try Open Graph description first
        Element ogDesc = doc.selectFirst("meta[property=og:description]");
        if (ogDesc != null && ogDesc.hasAttr("content")) {
            return ogDesc.attr("content");
        }

        // Try Twitter description
        Element twitterDesc = doc.selectFirst("meta[name=twitter:description]");
        if (twitterDesc != null && twitterDesc.hasAttr("content")) {
            return twitterDesc.attr("content");
        }

        // Try standard meta description
        Element metaDesc = doc.selectFirst("meta[name=description]");
        if (metaDesc != null && metaDesc.hasAttr("content")) {
            return metaDesc.attr("content");
        }

        return null;
    }

    private String extractThumbnail(Document doc, String baseUrl) {
        // Try Open Graph image first
        Element ogImage = doc.selectFirst("meta[property=og:image]");
        if (ogImage != null && ogImage.hasAttr("content")) {
            return resolveUrl(ogImage.attr("content"), baseUrl);
        }

        // Try Twitter image
        Element twitterImage = doc.selectFirst("meta[name=twitter:image]");
        if (twitterImage != null && twitterImage.hasAttr("content")) {
            return resolveUrl(twitterImage.attr("content"), baseUrl);
        }

        // Try to find the first significant image
        Element firstImage = doc.selectFirst("article img, .content img, img");
        if (firstImage != null && firstImage.hasAttr("src")) {
            return resolveUrl(firstImage.attr("src"), baseUrl);
        }

        return null;
    }

    private String extractFavicon(Document doc, String baseUrl) {
        // Try standard favicon link
        Element favicon = doc.selectFirst("link[rel~=(?i)^(shortcut )?icon]");
        if (favicon != null && favicon.hasAttr("href")) {
            return resolveUrl(favicon.attr("href"), baseUrl);
        }

        // Try apple-touch-icon
        Element appleFavicon = doc.selectFirst("link[rel=apple-touch-icon]");
        if (appleFavicon != null && appleFavicon.hasAttr("href")) {
            return resolveUrl(appleFavicon.attr("href"), baseUrl);
        }

        // Default to /favicon.ico
        try {
            URI uri = new URI(baseUrl);
            return uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
        } catch (Exception e) {
            return null;
        }
    }

    private String extractSiteName(Document doc, String baseUrl) {
        // Try Open Graph site name
        Element ogSiteName = doc.selectFirst("meta[property=og:site_name]");
        if (ogSiteName != null && ogSiteName.hasAttr("content")) {
            return ogSiteName.attr("content");
        }

        // Try to extract from domain
        try {
            URI uri = new URI(baseUrl);
            String host = uri.getHost();
            if (host != null) {
                // Remove www. and get first part of domain
                host = host.replaceFirst("^www\\.", "");
                String[] parts = host.split("\\.");
                if (parts.length > 0) {
                    // Capitalize first letter
                    String name = parts[0];
                    return name.substring(0, 1).toUpperCase() + name.substring(1);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private String extractAuthor(Document doc) {
        // Try article:author
        Element ogAuthor = doc.selectFirst("meta[property=article:author]");
        if (ogAuthor != null && ogAuthor.hasAttr("content")) {
            return ogAuthor.attr("content");
        }

        // Try standard author meta tag
        Element metaAuthor = doc.selectFirst("meta[name=author]");
        if (metaAuthor != null && metaAuthor.hasAttr("content")) {
            return metaAuthor.attr("content");
        }

        // Try Twitter creator
        Element twitterCreator = doc.selectFirst("meta[name=twitter:creator]");
        if (twitterCreator != null && twitterCreator.hasAttr("content")) {
            return twitterCreator.attr("content");
        }

        return null;
    }

    private LocalDateTime extractPublishedDate(Document doc) {
        // Try article:published_time
        Element ogPublished = doc.selectFirst("meta[property=article:published_time]");
        if (ogPublished != null && ogPublished.hasAttr("content")) {
            return parseDate(ogPublished.attr("content"));
        }

        // Try datePublished (Schema.org)
        Element schemaPublished = doc.selectFirst("meta[itemprop=datePublished]");
        if (schemaPublished != null && schemaPublished.hasAttr("content")) {
            return parseDate(schemaPublished.attr("content"));
        }

        return null;
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            // Try ISO 8601 format
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e1) {
            try {
                // Try without time
                return LocalDateTime.parse(dateStr + "T00:00:00", DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException e2) {
                log.warn("Could not parse date: {}", dateStr);
                return null;
            }
        }
    }

    private String resolveUrl(String url, String baseUrl) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // If already absolute URL, return as-is
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // Resolve relative URL
        try {
            URI base = new URI(baseUrl);
            URI resolved = base.resolve(url);
            return resolved.toString();
        } catch (Exception e) {
            log.warn("Could not resolve URL: {} with base: {}", url, baseUrl);
            return url;
        }
    }
}
