package com.vwo.framework.base;

import com.vwo.framework.config.ConfigReader;
import com.vwo.framework.driver.DriverManager;
import com.vwo.framework.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverManager.getDriver();
        driver.get(ConfigReader.get("base.url"));
        loginPage = new LoginPage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
