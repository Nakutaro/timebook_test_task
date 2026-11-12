package tests;

import fixtures.WebFixture;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchPage;

import java.time.LocalDate;
import java.util.List;

@Epic("Веб-приложение поиска сотрудников")
@Feature("Страница поиска")
public class SearchTest extends WebFixture {

    private SearchPage searchPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeSearch() {
        searchPage = new LoginPage()
                .logIn("test@test.test", "test")
                .isPageLoaded();
    }

    @Test(description = "После входа отображаются все 8 сотрудников по умолчанию")
    public void defaultEmployeeList() {
        searchPage
                .verifyResultsCount(8)
                .verifyEmployeeInTable(0, "ivanov@company.ru", "Иван Иванов");
    }

    @Test(description = "Поиск по точному Email")
    public void searchByEmail() {
        searchPage
                .filterByEmail("petrova@company.ru")
                .clickSearch()
                .verifyResultsCount(1)
                .verifyEmployeeInTable(0, "petrova@company.ru", "Мария Петрова");
    }

    @Test(description = "Поиск по частичному совпадению ФИО без учёта регистра")
    public void searchByPartialName() {
        searchPage
                .filterByName("иван")
                .clickSearch()
                .verifyResultsCount(1)
                .verifyEmployeeInTable(0, "ivanov@company.ru", "Иван Иванов");
    }

    @Test(description = "Фильтрация по полу")
    public void searchByGender() {
        searchPage
                .selectGender("Женский")
                .clickSearch()
                .verifyResultsCount(4)
                .verifyFilteredByGender("Женский");
    }

    @Test(description = "Фильтрация по возрасту")
    public void searchByAge() {
        searchPage
                .filterByAge(26)
                .clickSearch()
                .verifyResultsCount(1)
                .verifyEmployeeInTable(0, "sidorov@company.ru", "Алексей Сидоров");
    }

    @Test(description = "Фильтрация по диапазону дат приёма")
    public void searchByDateRange() {
        searchPage
                .filterByDateFrom(LocalDate.of(2022, 1, 1))
                .filterByDateTo(LocalDate.of(2024, 12, 31))
                .clickSearch()
                .verifyResultsCount(3);
    }

    @Test(description = "Фильтрация по нескольким навыкам")
    public void searchByMultipleSkills() {
        List<String> expectedSkills = List.of("Java", "API");

        searchPage
                .selectSkills("Java", "API")
                .clickSearch()
                .verifyResultsCount(2)
                .verifyFilteredBySkills(expectedSkills);
    }

    @Test(description = "Комбинированный поиск по нескольким фильтрам одновременно")
    public void combinedSearch() {
        searchPage
                .selectGender("Женский")
                .selectSkills("JS", "API")
                .filterByAge(30)
                .clickSearch()
                .verifyResultsCount(1)
                .verifyEmployeeInTable(0, "novikova@company.ru", "Ольга Новикова");
    }

    @Test(description = "Пустой результат поиска при отсутствии совпадений")
    public void searchWithNoResults() {
        searchPage
                .filterByName("Несуществующий Сотрудник")
                .clickSearch()
                .verifyEmptyResults();
    }

    @Test(description = "Ошибка валидации при неверном формате Email в форме поиска")
    public void searchWithInvalidEmailFormat() {
        searchPage
                .filterByEmail("ivanov")
                .clickSearch()
                .verifyEmailError();
    }

    @Test(description = "Ошибка валидации, если дата «с» позже даты «по»")
    public void searchWithInvalidDateRange() {
        searchPage
                .filterByDateFrom(LocalDate.of(2024, 1, 1))
                .filterByDateTo(LocalDate.of(2022, 1, 1))
                .clickSearch()
                .verifyDateError();
    }

    @Test(description = "Кнопка «Сбросить» очищает фильтры и возвращает полный список сотрудников")
    public void resetFiltersRestoresFullList() {
        searchPage
                .filterByEmail("ivanov@company.ru")
                .filterByName("Иван")
                .selectGender("Мужской")
                .filterByAge(28)
                .filterByDateFrom(LocalDate.of(2022, 1, 1))
                .filterByDateTo(LocalDate.of(2024, 12, 31))
                .selectSkills("Selenium", "API")
                .clickSearch()
                .verifyResultsCount(1)
                .clickReset()
                .verifyDefaultFilters()
                .verifyResultsCount(8);
    }
}
