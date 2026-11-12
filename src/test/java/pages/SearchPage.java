package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SetValueOptions;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SearchPage {

    private final SelenideElement searchPage = $("#searchPage");
    private final SelenideElement pageTitle = $("#searchPage h1");
    private final SelenideElement userInfo = $("#authUserEmail");
    private final SelenideElement buttonLogOut = $("#logoutButton");

    private final SelenideElement inputEmail = $("#searchEmail");
    private final SelenideElement inputName = $("#searchName");
    private final SelenideElement selectGender = $("#searchGender");
    private final SelenideElement inputAge = $("#searchAge");
    private final SelenideElement inputDateFrom = $("#searchDateFrom");
    private final SelenideElement inputDateTo = $("#searchDateTo");
    private final ElementsCollection checkboxesSkills = $$("input[name='searchSkill']");

    private final SelenideElement buttonSearch = $("#searchButton");
    private final SelenideElement buttonReset = $("#resetSearchButton");

    private final SelenideElement containerInvalidMail = $("#searchEmailFormatError");
    private final SelenideElement containerInvalidDate = $("#searchDateRangeError");

    private final SelenideElement resultsCount = $("#resultsCount");
    private final SelenideElement lastSearchInfo = $("#lastSearchInfo");
    private final ElementsCollection resultRows = $$("#resultsTable tbody tr");
    private final SelenideElement containerResultEmpty = $("#resultsEmpty");

    @Step("Проверяем, что страница поиска сотрудников загрузилась")
    public SearchPage isPageLoaded() {
        searchPage.shouldBe(visible);
        pageTitle.shouldHave(text("Поиск сотрудников"));
        return this;
    }

    @Step("Проверить, что в шапке отображается email пользователя '{expectedEmail}'")
    public SearchPage verifyLogIn(String expectedEmail) {
        userInfo.shouldHave(exactText(expectedEmail));
        return this;
    }

    @Step("Нажимаем кнопку «Выйти»")
    public LoginPage logOut() {
        buttonLogOut.click();
        return new LoginPage();
    }

    // Методы заполнения фильтров
    @Step("Вводим Email в фильтр поиска: '{email}'")
    public SearchPage filterByEmail(String email) {
        inputEmail.setValue(email);
        return this;
    }

    @Step("Вводим ФИО в фильтр поиска: '{name}'")
    public SearchPage filterByName(String name) {
        inputName.setValue(name);
        return this;
    }

    @Step("Выбираем пол в фильтре поиска: '{gender}'")
    public SearchPage selectGender(String gender) {
        selectGender.selectOption(gender);
        return this;
    }

    @Step("Вводим возраст в фильтр поиска: '{age}'")
    public SearchPage filterByAge(int age) {
        inputAge.setValue(String.valueOf(age));
        return this;
    }

    @Step("Указываем дату приёма «с»: '{date}'")
    public SearchPage filterByDateFrom(LocalDate date) {
        inputDateFrom.setValue(SetValueOptions.withDate(date));
        return this;
    }

    @Step("Указываем дату приёма «по»: '{date}'")
    public SearchPage filterByDateTo(LocalDate date) {
        inputDateTo.setValue(SetValueOptions.withDate(date));
        return this;
    }

    @Step("Выбираем навыки в фильтре поиска: {skills}")
    public SearchPage selectSkills(String... skills) {
        for (String skill : skills) {
            $("input[name='searchSkill'][value='" + skill + "']").setSelected(true);
        }
        return this;
    }

    @Step("Нажимаем кнопку «Найти»")
    public SearchPage clickSearch() {
        buttonSearch.click();
        return this;
    }

    @Step("Нажимаем кнопку «Сбросить»")
    public SearchPage clickReset() {
        buttonReset.click();
        return this;
    }

    @Step("Проверяем, что количество найденных сотрудников равно {expectedCount}")
    public SearchPage verifyResultsCount(int expectedCount) {
        resultsCount.shouldHave(exactText("Найдено: " + expectedCount));
        resultRows.shouldHave(size(expectedCount));
        if (expectedCount > 0) {
            containerResultEmpty.shouldBe(hidden);
        }
        return this;
    }

    @Step("Проверяем отображение пустого результата поиска")
    public SearchPage verifyEmptyResults() {
        resultsCount.shouldHave(exactText("Найдено: 0"));
        resultRows.shouldHave(size(0));
        containerResultEmpty.shouldBe(visible)
                .shouldHave(exactText("По заданным фильтрам ничего не найдено"));
        return this;
    }

    @Step("Проверяем, что в строке {rowIndex} отображается сотрудник '{expectedName}' ({expectedEmail})")
    public SearchPage verifyEmployeeInTable(int rowIndex, String expectedEmail, String expectedName) {
        SelenideElement row = resultRows.get(rowIndex);
        row.$$("td").get(1).shouldHave(exactText(expectedEmail));
        row.$$("td").get(2).shouldHave(exactText(expectedName));
        return this;
    }

    @Step("Проверяем, что у всех найденных сотрудников указан пол '{expectedGender}'")
    public SearchPage verifyFilteredByGender(String expectedGender) {
        for (SelenideElement row : resultRows) {
            row.$$("td").get(3).shouldHave(exactText(expectedGender));
        }
        return this;
    }

    @Step("Проверяем, что у всех найденных сотрудников присутствуют навыки: {expectedSkills}")
    public SearchPage verifyFilteredBySkills(List<String> expectedSkills) {
        for (SelenideElement row : resultRows) {
            SelenideElement skillsCell = row.$$("td").get(7);
            for (String skill : expectedSkills) {
                skillsCell.shouldHave(text(skill));
            }
        }
        return this;
    }

    @Step("Проверяем отображение ошибки неверного формата Email в поиске")
    public SearchPage verifyEmailError() {
        containerInvalidMail.shouldBe(visible)
                .shouldHave(text("Проверьте Email"))
                .shouldHave(text("Неверный формат Email"));
        inputEmail.shouldHave(cssClass("input-error"));
        return this;
    }

    @Step("Проверяем отображение ошибки некорректного периода дат («с» позже «по»)")
    public SearchPage verifyDateError() {
        containerInvalidDate.shouldBe(visible)
                .shouldHave(text("Некорректный период"))
                .shouldHave(text("Дата «с» не может быть позже даты «по»"));
        return this;
    }

    @Step("Проверяем, что все фильтры сброшены в исходное состояние")
    public SearchPage verifyDefaultFilters() {
        inputEmail.shouldBe(empty);
        inputName.shouldBe(empty);
        selectGender.shouldHave(value(""));
        inputAge.shouldBe(empty);
        inputDateFrom.shouldBe(empty);
        inputDateTo.shouldBe(empty);
        for (SelenideElement checkbox : checkboxesSkills) {
            checkbox.shouldNotBe(selected);
        }
        lastSearchInfo.shouldHave(text("Фильтры сброшены. Показаны все записи."));
        return this;
    }
}
