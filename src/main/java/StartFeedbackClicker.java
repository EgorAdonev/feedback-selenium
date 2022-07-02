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
    void setUp() throws Exception
    {
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

    private void searchElements() throws Exception{
        driver.get(baseUrl);
        System.out.println(String.format(ruLocale,driver.getTitle()));
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

//      WebElement showAllButton = driver.findElement(By.className("recommended-vacancies__button"));
        WebElement showAllVacanciesButton = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.
                        presenceOfElementLocated(By.xpath("/html/body/div[4]/div/div[3]/div[1]/div[2]/div/div[1]/div[3]/div/div/div[11]/div/span/a")
                        ));
        showAllVacanciesButton.click();

        //minimize browser window
//        Robot robot = new Robot();
//        robot.keyPress(KeyEvent.VK_ALT);
//        robot.keyPress(KeyEvent.VK_SPACE);
//        robot.keyPress(KeyEvent.VK_N);
//        robot.keyRelease(KeyEvent.VK_ALT);
//        robot.keyRelease(KeyEvent.VK_SPACE);
//        robot.keyRelease(KeyEvent.VK_N);
        WebElement searchResult = driver.findElement(By.className("vacancy-serp-content"));//search-results

        List<WebElement> vacancyItem = searchResult.findElements(By.className("vacancy-serp-item"));
        for(WebElement element : vacancyItem){
            WebElement companyInfo = element.findElement(By.className("vacancy-serp-item-company"));
            if(companyInfo.getText().contains("Сбер")||companyInfo.getText().contains("СБЕР")){
//                System.out.println(String.format("Информация:%s\n", companyInfo.getText(),ruLocale));
                continue;
            } else {
//                System.out.println(String.format("Информация:%s\n", companyInfo.getText(),ruLocale));
                WebElement feedbackButton = element.findElement(By.className("vacancy-serp-actions"));
                feedbackButton.findElement(By.tagName("a")).click();
            }
        }
    }
}