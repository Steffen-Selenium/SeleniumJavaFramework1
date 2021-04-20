package de.simple.testcase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import pageobject.OrderBox;

public class Preisvergleich {

	public static WebDriver driver;
	public String baseUrl;

	public ExtentHtmlReporter reporter;
	public ExtentReports extent;
	public ExtentTest test;

	@BeforeClass
	@Parameters({ "browser" })
	public void setUp(String browserName) {
		/*
		 * if (driver == null) { System.setProperty("webdriver.chrome.driver",
		 * System.getProperty("user.dir") +
		 * "\\src\\test\\resources\\driver\\chromedriver.exe"); driver = new
		 * ChromeDriver();
		 */
		if (browserName.equalsIgnoreCase("firefox")) {
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + "\\src\\\\test\\resources\\driver\\geckodriver.exe");
			driver = new FirefoxDriver();

		} else if (browserName.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\driver\\chromedriver2.exe");
			driver = new ChromeDriver();

		} else if (browserName.equalsIgnoreCase("Edge")) {
			System.setProperty("webdriver.edge.driver",
					System.getProperty("user.dir") + "\\src\\test\\resources\\driver\\msedgedriver.exe");
			driver = new EdgeDriver();
		}

		// extent Reports
		reporter = new ExtentHtmlReporter("Report.html");

		extent = new ExtentReports();
		extent.attachReporter(reporter);
		reporter.loadXMLConfig(new File(System.getProperty("user.dir") + "\\extent-config.xml"));
		extent.setSystemInfo("Host Name", "LocalHost");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("User Name", "Steffen");
		// BeforeSuite

		baseUrl = "http://automationpractice.com/index.php";
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.get(baseUrl);

	}

	@Test(dataProvider = "PV", enabled = true)
	public void PreisinWarenkorb(int Anzahlint) {
		test = extent.createTest("PreisinWarenkorb");
		test.log(Status.INFO, "Anzahl Produkte Warenkorb: " + String.valueOf(Anzahlint));

		driver.get("http://automationpractice.com/index.php?id_product=5&controller=product");

		String Preis = driver.findElement(By.id("our_price_display")).getText();
		String Model = driver.findElement(By.id("product_reference")).getText();
		System.out.println(Model);
		// Assert.assertEquals(Preis + " " + Model, "$27.00 Model demo_2");

		String Anzahl = String.valueOf(Anzahlint);

		OrderBox order;

		order = new OrderBox(driver);
		order.Quantity(Anzahl);
		order.AddtoCart();

		// Gesamtpreis im Warenkorb berechnen:
		double PreisZahl = Double.parseDouble(Preis.replaceAll("[^\\d.]", ""));
		double AnzahlRechnen = Double.parseDouble(Anzahl);
		double Gesamtpreis = AnzahlRechnen * PreisZahl;

		System.out.println("Preis fuer ein Stueck: " + PreisZahl);

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Der Gesamtpris im Warenkorb ist: " + Gesamtpreis);

		driver.get("http://automationpractice.com/index.php?controller=order");
		driver.navigate().refresh();

		String TotalPreis = driver.findElement(By.id("total_product")).getText();
		double TotalPreisRechnen = Double.parseDouble(TotalPreis.replaceAll("[^\\d.]", ""));
		Assert.assertEquals(TotalPreisRechnen, Gesamtpreis);
		System.out.println("Preis ist gleich");
		driver.findElement(By.className("icon-trash")).click();
		test.log(Status.PASS, "Test  passed");
		driver.get("http://automationpractice.com/index.php?id_product=5&controller=product");
	}

	@Test
	public void failtest() {
		test = extent.createTest("FAILTEST");
		Assert.assertTrue(false);
	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL, result.getThrowable());
		}

		// extent.removeTest(test);
	}

	@AfterClass
	public void tearDowns() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// calling flush writes everything to the log file
		extent.flush();

		if (driver != null) {
			driver.quit();
		}

	}

	@DataProvider(name = "PV")
	public Object[][] readnumericvalue() throws IOException {

		String projectPath = System.getProperty("user.dir");
		String excelPath = (projectPath + "\\src\\test\\resources\\testdata\\LoginDaten.xlsx");
		File src = new File(excelPath);

		FileInputStream fis = new FileInputStream(src);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet1 = wb.getSheet("PreisV");

		int rowcount = sheet1.getPhysicalNumberOfRows();
		int columnCount = sheet1.getRow(0).getLastCellNum();
		Object objects[][] = new Object[rowcount - 1][columnCount];
		int rowCounter = 0;

		Iterator<Row> rowIterator = sheet1.iterator();
		boolean firstRow = true;
		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			if (firstRow) {
				firstRow = false;
				continue;
			}
			Iterator<Cell> cellIterator = currentRow.iterator();
			int colCounter = 0;
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellTypeEnum()) {
				case STRING:
					objects[rowCounter][colCounter] = cell.getStringCellValue();
					break;

				case NUMERIC:
					objects[rowCounter][colCounter] = new Double(cell.getNumericCellValue()).intValue();
					break;
				}
				colCounter++;
			}
			rowCounter++;
		}
		return objects;
	}

}
