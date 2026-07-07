package com.vwo.framework.dataproviders;

import com.vwo.framework.utils.JsonDataUtil;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Map;

public final class LoginDataProvider {

    private LoginDataProvider() {
    }

    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        List<Map<String, String>> records = JsonDataUtil.readRecords("testdata/login_data.json");
        Object[][] data = new Object[records.size()][1];
        for (int i = 0; i < records.size(); i++) {
            data[i][0] = records.get(i);
        }
        return data;
    }
}
