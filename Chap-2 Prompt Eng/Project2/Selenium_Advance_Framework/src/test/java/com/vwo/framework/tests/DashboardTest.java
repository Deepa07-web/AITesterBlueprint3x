package com.vwo.framework.tests;

import com.vwo.framework.base.BaseTest;
import com.vwo.framework.config.ConfigReader;
import com.vwo.framework.pages.DashboardPage;
import com.vwo.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    @Test(description = "TC_037: Verify successful authentication transitions the user to the VWO dashboard")
    public void testDashboardDisplayedAfterLogin() {
        DashboardPage dashboardPage = loginPage.login(
                ConfigReader.get("test.valid.email"),
                ConfigReader.get("test.valid.password"));

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Expected dashboard header to be visible");
    }

    @Test(description = "Verify logout from the dashboard returns the user to the login page")
    public void testLogoutReturnsToLoginPage() {
        DashboardPage dashboardPage = loginPage.login(
                ConfigReader.get("test.valid.email"),
                ConfigReader.get("test.valid.password"));

        LoginPage loginPageAfterLogout = dashboardPage.logout();
        Assert.assertTrue(loginPageAfterLogout.isLoginPageDisplayed(),
                "Expected to be returned to the login page after logout");
    }
}
