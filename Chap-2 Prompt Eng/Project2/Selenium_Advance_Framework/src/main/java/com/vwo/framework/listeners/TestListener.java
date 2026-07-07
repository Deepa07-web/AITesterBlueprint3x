package com.vwo.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.vwo.framework.driver.DriverManager;
import com.vwo.framework.reports.ExtentManager;
import com.vwo.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentManager.getInstance().createTest(
                result.getMethod().getMethodName(), result.getMethod().getDescription());
        ExtentManager.setTest(test);
        LOGGER.info("Starting test: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test passed");
        LOGGER.info("Test passed: {}", result.getMethod().getMethodName());
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LOGGER.error("Test failed: {}", testName, result.getThrowable());

        String screenshotPath = ScreenshotUtils.capture(DriverManager.getDriver(), testName);
        ExtentTest test = ExtentManager.getTest();
        test.log(Status.FAIL, result.getThrowable());
        if (screenshotPath != null) {
            try {
                test.fail("Screenshot on failure",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                LOGGER.warn("Could not attach screenshot to Extent report", e);
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
        LOGGER.warn("Test skipped: {}", result.getMethod().getMethodName());
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }
}
