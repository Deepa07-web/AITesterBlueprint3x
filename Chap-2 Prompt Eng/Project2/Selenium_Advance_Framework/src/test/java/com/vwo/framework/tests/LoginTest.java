package com.vwo.framework.tests;

import com.vwo.framework.base.BaseTest;
import com.vwo.framework.config.ConfigReader;
import com.vwo.framework.dataproviders.LoginDataProvider;
import com.vwo.framework.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseTest {

    @Test(description = "TC_001: Verify successful login with valid email and password")
    public void testSuccessfulLogin() {
        DashboardPage dashboardPage = loginPage.login(
                ConfigReader.get("test.valid.email"),
                ConfigReader.get("test.valid.password"));

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Expected dashboard to be displayed after successful login");
    }

    @Test(description = "TC_002-005/014: Verify login is rejected/blocked for invalid credentials",
            dataProvider = "invalidLoginData", dataProviderClass = LoginDataProvider.class)
    public void testInvalidLoginScenarios(Map<String, String> data) {
        loginPage.enterEmail(data.get("email"));
        loginPage.enterPassword(data.get("password"));
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isEmailFormatErrorDisplayed()
                        || loginPage.isToastMessageDisplayed()
                        || loginPage.isLoginPageDisplayed(),
                "[" + data.get("scenarioTid") + "] Expected login to be rejected/blocked for: "
                        + data.get("description"));
    }

    @Test(description = "TC_018: Verify Forgot Password initiates the password reset flow")
    public void testForgotPasswordFlowInitiates() {
        loginPage.clickForgotPassword();
        Assert.assertTrue(loginPage.isForgotPasswordFormDisplayed(),
                "Expected the forgot-password form to be displayed after clicking Forgot Password");
    }

    @Test(description = "TC_036: Verify the product announcement banner is displayed on the login page")
    public void testAnnouncementBannerDisplayed() {
        Assert.assertTrue(loginPage.isAnnouncementBannerDisplayed(),
                "Expected announcement banner to be visible on login page");
    }
}
