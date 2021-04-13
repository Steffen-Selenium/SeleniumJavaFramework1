package de.simple.testbase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Testbase {
	private WebDriver driver;

	@Test
	public void simpletest() {

		System.setProperty("webdriver.chrome.driver",
				System.getProperty("user.dir") + "\\src\\test\\resources\\driver\\chromedriver2.exe");
		driver = new ChromeDriver();
		driver.get("http://www.google.de");
		driver.manage().window().maximize();
		Assert.assertEquals(true, false);
		driver.close();
	}

}
