package fixtures;

import com.codeborne.selenide.*;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
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
    public void openApplication(@Optional String browser) {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));

        if (browser != null && !browser.isEmpty()) {
            Configuration.browser = browser;
        }

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
