package tests;

import fixtures.WebFixture;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchPage;

@Epic("Веб-приложение поиска сотрудников")
@Feature("Страница авторизации")
public class LoginTest extends WebFixture {

    private static final String TEST_MAIL = "test@test.test";
    private static final String TEST_PWD = "test";

    @DataProvider(name = "invalidEmails", parallel = true)
    public Object[][] invalidEmails() {
        return new Object[][]{
                {""},
                {"egwgwrhwrh"},
                {"wewefw@domwefwefain"},
                {"uwfwefser@.com"},
                {"usdfsdfser@sdgs..com"},
                {"sdfsdf namsdfsdfse@sdf.ru"}
        };
    }

    @DataProvider(name = "invalidCredentials", parallel = true)
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {TEST_MAIL, "invalid_password"},
                {"invalid@test.test", TEST_PWD},
                {TEST_MAIL, ""}
        };
    }

    @Test(description = "Успешная авторизация с валидными данными")
    public void login() {
        new LoginPage()
                .isPageLoaded()
                .logIn(TEST_MAIL, TEST_PWD)
                .isPageLoaded()
                .verifyLogIn(TEST_MAIL);
    }

    @Test(description = "Успешная авторизация по нажатию Enter")
    public void loginByEnter() {
        new LoginPage()
                .isPageLoaded()
                .fillLoginForm(TEST_MAIL, TEST_PWD)
                .submitByEnter();

        new SearchPage()
                .isPageLoaded()
                .verifyLogIn(TEST_MAIL);
    }

    @Test(dataProvider = "invalidEmails", description = "Ошибка валидации при неверном формате Email")
    public void loginWithInvalidEmailFormat(String invalidEmail) {
        new LoginPage()
                .isPageLoaded()
                .fillLoginForm(invalidEmail, TEST_PWD)
                .clickSubmit()
                .verifyEmailFormatError();
    }

    @Test(dataProvider = "invalidCredentials", description = "Ошибка при неверном Email и/или пароле")
    public void loginWithWrongCredentials(String email, String password) {
        new LoginPage()
                .isPageLoaded()
                .fillLoginForm(email, password)
                .clickSubmit()
                .verifyAuthError();
    }

    @Test(description = "Выход из системы возвращает на форму авторизации и очищает поля")
    public void logOut() {
        new LoginPage()
                .logIn(TEST_MAIL, TEST_PWD)
                .isPageLoaded()
                .logOut()
                .isPageLoaded()
                .checkFieldsEmpty();
    }

    @Test(description = "Проверка закрытия уведомления об ошибке")
    public void verifyCloseErrorMsg() {
        new LoginPage()
                .isPageLoaded()
                .fillLoginForm("invalidEmail", TEST_PWD)
                .clickSubmit()
                .verifyEmailFormatError()
                .closeErrorContainer();
        }
}
