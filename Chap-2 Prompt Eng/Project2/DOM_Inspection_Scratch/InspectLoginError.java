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
 * Standalone reference tool - triggers a real failed login on app.vwo.com to capture
 * whatever error/toast element the app actually renders, so LoginPage's error locator
 * is verified instead of guessed.
 */
public class InspectLoginError {

    public static void main(String[] args) throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://app.vwo.com/#/login");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("login-username")));

            driver.findElement(By.id("login-username")).sendKeys("bogus_probe_user@example.com");
            driver.findElement(By.id("login-password")).sendKeys("BogusPassword123!");
            driver.findElement(By.id("js-login-btn")).click();

            Thread.sleep(4000); // allow async validation/toast to render

            List<WebElement> candidates = driver.findElements(
                    By.cssSelector(".invalid-reason, .toast, [class*='toast'], [class*='error'], [ng-show='errorMessage']"));
            String dump = candidates.stream()
                    .map(e -> "displayed=" + e.isDisplayed() + " | outerHTML=" + e.getAttribute("outerHTML"))
                    .collect(Collectors.joining("\n---\n"));

            Files.writeString(Path.of("login-error-dump.txt"), dump.isEmpty() ? "NO MATCHING ELEMENTS FOUND" : dump);
            System.out.println("Wrote login-error-dump.txt (" + candidates.size() + " candidates)");
        } finally {
            driver.quit();
        }
    }
}
