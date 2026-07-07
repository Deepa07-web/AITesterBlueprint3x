package com.vwo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * PLACEHOLDER LOCATORS: see LoginPage note - no real DOM/screenshots were provided for the
 * post-login VWO dashboard, so these selectors are structural placeholders pending real
 * app inspection.
 */
public class DashboardPage extends BasePage {

    private static final By DASHBOARD_HEADER = By.cssSelector(".dashboard-header");
    private static final By WELCOME_MESSAGE = By.cssSelector(".welcome-message");
    private static final By USER_PROFILE_MENU = By.cssSelector(".user-profile-menu");
    private static final By LOGOUT_BUTTON = By.linkText("Logout");
    private static final By RECENT_ACTIVITY_PANEL = By.cssSelector(".recent-activity-panel");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardDisplayed() {
        return isDisplayed(DASHBOARD_HEADER);
    }

    public String getWelcomeMessage() {
        return getText(WELCOME_MESSAGE);
    }

    public boolean isRecentActivityDisplayed() {
        return isDisplayed(RECENT_ACTIVITY_PANEL);
    }

    public LoginPage logout() {
        click(USER_PROFILE_MENU);
        click(LOGOUT_BUTTON);
        return new LoginPage(driver);
    }
}
