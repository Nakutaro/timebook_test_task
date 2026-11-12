package fixtures;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.net.URL;
import java.util.Objects;

public abstract class WebFixture {

    private static final Logger log = LoggerFactory.getLogger(WebFixture.class);

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    @Step("Открытие браузера")
    public void openApplication(@Optional("chrome") String browser) {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));

        String targetBrowser = System.getProperty("selenide.browser", browser);
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));

        SelenideConfig config = new SelenideConfig()
                .browser(targetBrowser)
                .browserSize("1920x1080")
                .timeout(6000)
                .headless(isHeadless);

        SelenideDriver driver = new SelenideDriver(config);
        WebDriverRunner.setWebDriver(driver.getAndCheckWebDriver());

        URL pageUrl = Objects.requireNonNull(
                getClass().getClassLoader().getResource("index.html"),
                "Файл index.html не найден в src/test/resources/"
        );
        Selenide.open(pageUrl);
    }

    @Step("Закрытие браузера")
    @AfterMethod(alwaysRun = true)
    public void teardown() {
        try {
            if (WebDriverRunner.hasWebDriverStarted()) {
                attachPageSource();

                if (WebDriverRunner.getWebDriver() instanceof RemoteWebDriver remoteDriver) {
                    SessionId sessionId = remoteDriver.getSessionId();
                    log.info("[{}] Browser session closed: {}", Thread.currentThread().getName(), sessionId);
                }
            }
        } finally {
            Selenide.closeWebDriver();
        }
    }

    @Step("Прикрепить исходный код страницы к отчёту")
    private void attachPageSource() {
        Allure.addAttachment("Page Source (HTML)", "text/html", WebDriverRunner.source(), ".html");
    }
}
