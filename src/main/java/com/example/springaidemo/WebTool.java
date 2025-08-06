package com.example.springaidemo;


import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.springframework.ai.tool.annotation.Tool;

import java.io.IOException;

public class WebTool {

    @Tool(description = "Fetches the content of a web page", name = "getWebPage")
    public String getWebPage(String url) {

        System.out.println("fetching web page: " + url);
        try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.waitForBackgroundJavaScriptStartingBefore(4000);
            webClient.waitForBackgroundJavaScript(4000);
            webClient.getOptions().setTimeout(20000);

            // Configure error handling
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);

            // Configure CSS handling
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setDownloadImages(false);

            // Configure JavaScript handling
            webClient.getOptions().setJavaScriptEnabled(true);


            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setMaxInMemory(0);

            HtmlPage page = webClient.getPage(url);
            waitForPageToLoad(webClient, page, 10000);

            String content = page.asXml();
            return content;
        } catch (IOException e) {
            System.err.println("Error fetching web page: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "no result";
    }

    private void waitForPageToLoad(WebClient webClient, HtmlPage page, int maxWaitTimeMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int checkInterval = 500; // Check every 500ms
        String previousContent = "";
        int stableCount = 0;
        int requiredStableChecks = 3; // Page must be stable for 3 checks (1.5 seconds)

        System.out.println("Waiting for SPA content to load...");

        while ((System.currentTimeMillis() - startTime) < maxWaitTimeMs) {
            // Wait for background JavaScript
            webClient.waitForBackgroundJavaScript(checkInterval);

            // Get current page content
            String currentContent = page.asXml();

            // Check if content has stabilized
            if (currentContent.equals(previousContent)) {
                stableCount++;
                if (stableCount >= requiredStableChecks) {
                    System.out.println("SPA content appears stable after " +
                            (System.currentTimeMillis() - startTime) + "ms");
                    break;
                }
            } else {
                stableCount = 0; // Reset stability counter
                previousContent = currentContent;
            }

            // Small sleep to prevent busy waiting
            Thread.sleep(100);
        }

        // Final wait for any remaining background processes
        webClient.waitForBackgroundJavaScript(1000);

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Total wait time for SPA: " + totalTime + "ms");
    }

}
