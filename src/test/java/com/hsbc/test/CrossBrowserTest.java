package com.hsbc.test;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HSBC on 02-05-2017.
 */
public class CrossBrowserTest {
    protected final String BROWSER = System.getProperty("BROWSER", "ie");
    protected final String APPLICATION_URL = "http://vm-dev.eastus.cloudapp.azure.com:8080/CreditCardApp/";

    RemoteWebDriver driver;
    DesiredCapabilities capabilities;
    WebDriverWait wait;

    // Application specific test data

    String userName 		= "ashukla";
    String mobileNumber 	= "99530";
    String city 			= "Lucknow";
    String creditCardType 	= "silver";
    String expectedPage 	= "CREDIT CARD APPLICATION DETAILS";

    // Application specific elements

    By userNameInput = By.name("name");
    By numberInput = By.name("number");
    By cityInput = By.name("city");
    By creditTypeList = By.name("creditCard");
    By submitButton = By.id("submit-button");//By.xpath("//input[@value='SUBMIT']");
    By confirmText = By.xpath("//*[contains(text(),'WELCOME')]");

    @BeforeTest
    public void setup() {
        String executable;
        switch (BROWSER){
            case "chrome":
                executable = System.getProperty("user.dir") + "/lib/chromedriver.exe";
                System.setProperty("webdriver.chrome.driver", executable);
                //driver = new ChromeDriver();
                capabilities = DesiredCapabilities.chrome();
                break;
            case "ie":
                executable = System.getProperty("user.dir") + "/lib/IEDriverServer.exe";
                System.setProperty("webdriver.ie.driver", executable);
                //driver = new InternetExplorerDriver();
                capabilities = DesiredCapabilities.internetExplorer();
		//driver.switchTo().alert().accept();                   // Uncomment this to fix
		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
                break;
            default:
                System.out.println("Browser Unsupported");
                break;
        }
        try {
            driver = new RemoteWebDriver(new URL("http://40.71.175.242:4444/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        wait = new WebDriverWait(driver, 20);
        driver.get(APPLICATION_URL);
        driver.manage().window().maximize();
    }

    @Test
    public void test(){
        String title = "Credit Card Application";
        Assert.assertEquals(title, driver.getTitle());

        // Deal with userName
        driver.findElement(userNameInput).click();
        driver.findElement(userNameInput).clear();
        driver.findElement(userNameInput).sendKeys(userName);

        // deal with number
        driver.findElement(numberInput).click();
        driver.findElement(numberInput).clear();
        driver.findElement(numberInput).sendKeys(mobileNumber);

        // deal with city
        driver.findElement(cityInput).click();
        driver.findElement(cityInput).clear();
        driver.findElement(cityInput).sendKeys(city);

        //deal with credit card type
		/*WebElement creditType = driver.findElement(creditTypeList);
		Select select = new Select(creditType);
		select.selectByValue(creditCardType);*/

        driver.findElement(submitButton).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(confirmText));
        // Validating that the next page is up
        Assert.assertTrue(driver.getPageSource().contains(expectedPage));
	/*if (BROWSER.equals("ie")) {
            Assert.fail("The Application is not supported on IE");
        }*/

    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
