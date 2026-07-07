import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Standalone reference tool - NOT part of the Selenium_Advance_Framework build.
 * One-off DOM inspection used to discover real locators for LoginPage/DashboardPage
 * after placeholder locators failed against the live app.vwo.com login page.
 *
 * Compile/run (from this folder, using the framework's Maven-resolved classpath):
 *   javac -cp "%CP%" InspectPage.java
 *   java  -cp ".;%CP%" InspectPage <url>
 */
public class InspectPage {

    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : "https://app.vwo.com";

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfElementLocated(By.tagName("input")));

            List<WebElement> interactive = driver.findElements(By.cssSelector("input, button, a, form"));
            String dump = interactive.stream()
                    .map(e -> "TAG=" + e.getTagName() + " | outerHTML=" + e.getAttribute("outerHTML"))
                    .collect(Collectors.joining("\n---\n"));

            Files.writeString(Path.of("dom-dump.txt"), "URL: " + driver.getCurrentUrl() + "\n\n" + dump);
            System.out.println("Wrote dom-dump.txt (" + interactive.size() + " elements) for " + driver.getCurrentUrl());
        } finally {
            driver.quit();
        }
    }
}
