package com.vwo.framework.utils;

import com.vwo.framework.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public final class ScreenshotUtils {

    private static final Logger LOGGER = LogManager.getLogger(ScreenshotUtils.class);

    private ScreenshotUtils() {
    }

    public static String capture(WebDriver driver, String testName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String fileName = testName + "_" + timestamp + ".png";
        String dir = ConfigReader.get("screenshot.dir");

        try {
            Files.createDirectories(Path.of(dir));
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(dir, fileName);
            Files.copy(source.toPath(), destination.toPath());
            return destination.getPath();
        } catch (IOException e) {
            LOGGER.error("Failed to capture screenshot for test '{}'", testName, e);
            return null;
        }
    }
}
