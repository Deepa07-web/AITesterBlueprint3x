package com.restfulbooker.base;

import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.pages.AuthPage;
import com.restfulbooker.pages.BookingPage;
import org.testng.annotations.BeforeClass;

/**
 * Centralizes page-object instantiation and a fresh auth token per test class (RICE-POT:
 * Isolated — no hardcoded/shared token across the whole suite, avoids token-expiry flakiness).
 */
public abstract class BaseTest {

    protected AuthPage authPage;
    protected BookingPage bookingPage;
    protected String authToken;

    @BeforeClass(alwaysRun = true)
    public void baseSetup() {
        authPage = new AuthPage();
        bookingPage = new BookingPage();
        authToken = authPage.createTokenAndGet(
                ConfigManager.getDefaultUsername(),
                ConfigManager.getDefaultPassword());
    }
}
