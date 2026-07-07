package com.vwo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Locators below were captured by inspecting the live DOM at https://app.vwo.com/#/login
 * (see Chap-2 Prompt Eng/Project2/DOM_Inspection_Scratch/) - not guessed. Confidence noted
 * per field:
 *  VERIFIED  - observed directly in the real page's outerHTML.
 *  INFERRED  - element/component confirmed present in DOM, but the exact trigger condition
 *              (e.g. an actual "invalid credentials" message) was not directly observed.
 *  UNVERIFIED - never reached (requires a valid/2FA-enabled account); kept as placeholder.
 */
public class LoginPage extends BasePage {

    private static final By EMAIL_INPUT = By.id("login-username"); // VERIFIED
    private static final By PASSWORD_INPUT = By.id("login-password"); // VERIFIED
    private static final By LOGIN_BUTTON = By.id("js-login-btn"); // VERIFIED
    private static final By REMEMBER_ME_CHECKBOX = By.id("checkbox-remember"); // VERIFIED
    private static final By FORGOT_PASSWORD_BUTTON =
            By.cssSelector("button[onclick='login.gotoForgotPasswordView()']"); // VERIFIED - real element is a <button>, not a link
    private static final By SSO_LOGIN_BUTTON = By.cssSelector("button[onclick='login.goToSSOView()']"); // VERIFIED
    private static final By GOOGLE_SIGNIN_BUTTON = By.id("js-google-signin-btn"); // VERIFIED
    private static final By FREE_TRIAL_LINK =
            By.xpath("//a[.//span[normalize-space()='Start a FREE TRIAL']]"); // VERIFIED - real page has no "register" link, only this CTA
    private static final By EMAIL_FORMAT_ERROR =
            By.cssSelector("#js-login-form .invalid-reason"); // VERIFIED - client-side "Invalid email" validation message
    private static final By TOAST_MESSAGE = By.cssSelector(".toast-container [role='alert']"); // INFERRED - toast infra confirmed real, exact invalid-credentials copy not observed triggering
    private static final By ANNOUNCEMENT_BANNER =
            By.cssSelector("a[href*='vwo-wingify-aligning-our-domain']"); // VERIFIED - "VWO has transitioned to Wingify" banner link
    private static final By TWO_FA_CODE_INPUT = By.id("twoFactorCodePLACEHOLDER"); // UNVERIFIED - OTP form only renders after 2FA-enabled primary auth succeeds
    private static final By FORGOT_PASSWORD_EMAIL_INPUT = By.id("forgot-password-username"); // VERIFIED - clicking Forgot Password swaps forms client-side, no URL change

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    public LoginPage checkRememberMe() {
        click(REMEMBER_ME_CHECKBOX);
        return this;
    }

    public DashboardPage clickLogin() {
        click(LOGIN_BUTTON);
        return new DashboardPage(driver);
    }

    public LoginPage clickForgotPassword() {
        click(FORGOT_PASSWORD_BUTTON);
        return this;
    }

    public void clickFreeTrialLink() {
        click(FREE_TRIAL_LINK);
    }

    public void enterTwoFactorCode(String code) {
        type(TWO_FA_CODE_INPUT, code);
    }

    public void clickSsoLogin() {
        click(SSO_LOGIN_BUTTON);
    }

    public void clickGoogleSignIn() {
        click(GOOGLE_SIGNIN_BUTTON);
    }

    public DashboardPage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        return clickLogin();
    }

    public String getEmailFormatError() {
        return getText(EMAIL_FORMAT_ERROR);
    }

    public boolean isEmailFormatErrorDisplayed() {
        return isDisplayed(EMAIL_FORMAT_ERROR);
    }

    public String getToastMessage() {
        return getText(TOAST_MESSAGE);
    }

    public boolean isToastMessageDisplayed() {
        return isDisplayed(TOAST_MESSAGE);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(EMAIL_INPUT) && isDisplayed(PASSWORD_INPUT);
    }

    public boolean isAnnouncementBannerDisplayed() {
        return isDisplayed(ANNOUNCEMENT_BANNER);
    }

    public boolean isForgotPasswordFormDisplayed() {
        return isDisplayed(FORGOT_PASSWORD_EMAIL_INPUT);
    }
}
