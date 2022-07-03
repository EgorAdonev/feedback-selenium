import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

class StartFeedbackClicker extends Thread{
    private WebDriver driver;
    private String baseUrl;
    private static final Locale ruLocale = new Locale("ru","RU");
    private static final ResourceBundle messages = ResourceBundle.getBundle("LoginData");

    //TODO JFrame - windows, buttons
    @Override
    public void run()  {
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sleep(500);
            searchElements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        teardown();
    }
    void setUp() {
        System.setProperty("webdriver.chrome.driver","C:\\IntellijJavaProjects\\hh-feedback-selenium\\src\\main\\resources\\drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://hh.ru/account/login?backurl=%2F&hhtmFrom=main";
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }
     void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void searchElements() {
        driver.get(baseUrl);
        driver.manage().window().maximize();
        System.out.printf(ruLocale, (driver.getTitle()) + "%n");
        WebElement loginWithPasswordButton = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.
                        elementToBeClickable(By.xpath("/html/body/div[5]/div/div[3]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/div[2]/div/div/form/div[4]/button[2]"))
                );
        loginWithPasswordButton.click();
        WebElement loginInput = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.
                        presenceOfElementLocated(By.xpath("/html/body/div[5]/div/div[3]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/div[2]/div/form/div[1]/input"))
                );
        loginInput.clear();

        WebElement passInput = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.
                        presenceOfElementLocated(By.xpath("/html/body/div[5]/div/div[3]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/div[2]/div/form/div[2]/span/input")
                ));
        loginInput.clear();

        WebElement loginButton = new WebDriverWait(driver, 20)
                .until(ExpectedConditions.
                        elementToBeClickable(By.xpath("//*[@id=\"HH-React-Root\"]/div/div[3]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/div[2]/div/form/div[4]/div/button[1]"))
                );
        loginInput.sendKeys(messages.getString("login"));
        loginInput.submit();
        passInput.sendKeys(messages.getString("password"));
        passInput.submit();
        loginButton.submit();//or click() but it didn't work

//        WebElement showAllVacanciesButton = driver.findElement(By.className("recommended-vacancies__button"));
//        WebElement showAllVacanciesButton = new WebDriverWait(driver, 5)
//                    .until(ExpectedConditions.
//                            presenceOfElementLocated(By.xpath("/html/body/div[4]/div/div[3]/div[1]/div[2]/div/div[1]/div[3]/div/div/div[11]/div/span/a")
//                            ));
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        WebElement searchButton = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.
                        elementToBeClickable(By.linkText("Найти")));
        WebElement searchInputField = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.
                        elementToBeClickable(By.id("a11y-search-input")));

        searchInputField.sendKeys("Java");
        try {
            searchInputField.submit();
        } catch (WebDriverException e) {
//            if(searchInputField.isSelected()){
//
//            } else {
//                searchButton.click();
//            }
        }
//        searchButton.click();

        WebElement searchResult = driver.findElement(By.className("vacancy-serp-content"));//search-results

        List<WebElement> vacancyItems = searchResult.findElements(By.className("vacancy-serp-item"));

        long vacanciesFoundOnCurrentPage = vacancyItems.size();
        int allVacanciesClicked=0;
        System.out.println(vacanciesFoundOnCurrentPage);
            for(int i = 0; i < (long) vacancyItems.size(); i++){
                if(i == vacanciesFoundOnCurrentPage) {
                    driver.findElement(By.xpath("//*[@id=\"HH-React-Root\"]/div/div[3]/div[1]/div/div[3]/div[2]/div[2]/div/div[5]/div/a")).click();
                    vacancyItems = driver.findElement(By.className("vacancy-serp-content")).findElements(By.className("vacancy-serp-item"));
    //                vacanciesFoundOnCurrentPage = vacancyItems.stream().count();
                }
                WebElement element = vacancyItems.get(i);
                WebElement companyInfo = null;
                try {
                    companyInfo = element.findElement(By.className("vacancy-serp-item-body__main-info"));
                } catch (StaleElementReferenceException e) {
                    continue;
                }
                CharSequence companyText = companyInfo.getText();
                if(StringUtils.containsIgnoreCase(companyText,"Senior")||StringUtils.containsIgnoreCase(companyText,"Главный")
                        ||StringUtils.containsIgnoreCase(companyText,"Ведущий")||StringUtils.containsIgnoreCase(companyText,"Tech Lead")
                        || StringUtils.containsIgnoreCase(companyText, "Старший")) {
                    i++;
//                    continue;
                }
                try {
                    WebElement respondButton = driver.findElement(By.linkText("Откликнуться"));
    //                WebElement respondButton = new WebDriverWait(driver, 10)
    //                        .until(ExpectedConditions.elementToBeClickable(By
    //                        .xpath("/html/body/div[5]/div/div[3]/div[1]/div/div[3]/div[2]/div[2]/div/div[1]/div[2]/div/div[6]/a"))
    //                        );
    //                        .click();
                    jse.executeScript("arguments[0].click()", respondButton);
                } catch (StaleElementReferenceException | NoSuchElementException e){
                    try {
                        jse.executeScript("arguments[0].click()", driver.findElement(By.className("bloko-button")));
                    } catch (NoSuchElementException exp) {
                        jse.executeScript("arguments[0].click()", driver.findElement(By.className("bloko-modal-close-button")));
                        i++;
                        continue;
                    }
                }
                System.out.printf("Информация:%s\n%n", companyInfo.getText(),ruLocale);
                allVacanciesClicked++;
                try {
                    WebElement popup = new WebDriverWait(driver, 2)
                            .until(ExpectedConditions.
                                    presenceOfElementLocated(By.xpath("/html/body/div[12]/div/div[1]")
                                    ));
                    if (popup.isDisplayed()) {
                        popup.findElement(By.xpath("/html/body/div[12]/div/div[1]/div[5]/div/button")).click();
                        allVacanciesClicked++;
                    }
                } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e){
                    i++;
                    continue;
                }
    //            else {
    ////              System.out.println(String.format("Информация:%s\n", companyInfo.getText(),ruLocale));
    //                feedbackButton.findElement(By.tagName("a")).click();
    //            }
            }
        System.out.println(allVacanciesClicked);
    }
}