package com.vwo.framework.pages;

import com.vwo.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Locator-repository pattern (By constants + explicit wait per action) rather than
 * @FindBy/PageFactory - avoids stale-element issues on dynamic content and keeps every
 * interaction behind an explicit wait.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils waitUtils;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    protected void click(By locator) {
        waitUtils.waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitUtils.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitUtils.waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        return waitUtils.isDisplayedQuick(locator);
    }
}
