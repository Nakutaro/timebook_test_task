package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement authPage = $("#authPage");
    private final SelenideElement pageTitle = $("#authPage h1");
    private final SelenideElement inputEmail = $("#loginEmail");
    private final SelenideElement inputPwd = $("#loginPassword");
    private final SelenideElement buttonLogIn = $("#authButton");

    private final SelenideElement containerInvalidMail = $("#authAlertsHolder");
    private final SelenideElement containerIncorrectEmailPwd = $("#invalidEmailPassword");

    @Step("Проверяем, что страница авторизации открыта")
    public LoginPage isPageLoaded() {
        authPage.shouldBe(visible);
        pageTitle.shouldHave(text("Вход в систему"));
        return this;
    }

    @Step("Заполняем учетные данные: email = '{email}'")
    public LoginPage fillLoginForm(String email, String password) {
        inputEmail.setValue(email);
        inputPwd.setValue(password);
        return this;
    }

    @Step("Нажимаем кнопку «Вход»")
    public LoginPage clickSubmit() {
        buttonLogIn.click();
        return this;
    }

    @Step("Выполняем вход по нажатию клавиши Enter")
    public LoginPage submitByEnter() {
        inputPwd.pressEnter();
        return this;
    }

    @Step("Авторизовываемся в системе под пользователем '{email}'")
    public SearchPage logIn(String email, String password) {
        fillLoginForm(email, password);
        clickSubmit();
        return new SearchPage();
    }

    @Step("Проверяем отображение ошибки неверного формата Email")
    public LoginPage verifyEmailFormatError() {
        containerInvalidMail.shouldBe(visible)
                .shouldHave(text("Проверьте Email"))
                .shouldHave(text("Неверный формат Email"));
        inputEmail.shouldHave(cssClass("input-error"));
        return this;
    }

    @Step("Проверяем отображение ошибки неверного Email или пароля")
    public LoginPage verifyAuthError() {
        containerIncorrectEmailPwd.shouldBe(visible)
                .shouldHave(text("Ошибка входа"))
                .shouldHave(text("Неверный Email или пароль"));
        inputEmail.shouldHave(cssClass("input-error"));
        inputPwd.shouldHave(cssClass("input-error"));
        return this;
    }

    @Step("Закрываем уведомление об ошибке")
    public LoginPage closeErrorContainer() {
        containerInvalidMail.$(".form-alert__close").click();
        containerInvalidMail.shouldBe(empty);
        return this;
    }

    @Step("Проверям, что поля Email и Пароль пустые")
    public LoginPage checkFieldsEmpty() {
        inputEmail.shouldBe(empty);
        inputPwd.shouldBe(empty);
        return this;
    }
}