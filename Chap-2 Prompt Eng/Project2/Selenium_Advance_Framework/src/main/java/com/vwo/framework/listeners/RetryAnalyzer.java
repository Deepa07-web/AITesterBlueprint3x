package com.vwo.framework.listeners;

import com.vwo.framework.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetry = ConfigReader.getInt("retry.max.count");
        if (retryCount < maxRetry) {
            retryCount++;
            return true;
        }
        return false;
    }
}
