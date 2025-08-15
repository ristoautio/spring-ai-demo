package com.example.springaidemo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Disabled
class WebToolTest {

    private WebTool webTool;

    @BeforeEach
    void setUp() {
        webTool = new WebTool();
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testGetWebPageWithRealWebsite() {
        // Arrange
        String testUrl = "https://www.w3schools.com/html/";

        // Act
        String result = webTool.getWebPage(testUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertNotEquals("no result", result, "Should not return error message");
        assertTrue(result.contains("<html"), "Should contain HTML content");
        assertTrue(result.contains("</html>"), "Should contain closing HTML tag");
        assertTrue(result.contains("Herman Melville"), "Should contain expected content from httpbin.org/html");
        assertTrue(result.length() > 100, "Should return substantial content");
    }
}