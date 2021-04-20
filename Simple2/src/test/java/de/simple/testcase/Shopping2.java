package de.simple.testcase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import de.simple.pageobject.OrderBox;
import de.simple.testbase.Testbase;

public class Shopping2 extends Testbase {

	@Test(dataProvider = "dp2", priority = 1)
	public void Shopping_ShortsNegativString(int Anzahl, String Size, String AnzahlB) throws Exception {
		test = extent.createTest("Shopping_ShortsNegativString");

		driver.get(baseUrl);

		OrderBox order;

		order = new OrderBox(driver);

		driver.findElement(By.xpath("//*[@id=\"homefeatured\"]/li[2]/div/div[1]/div/a[1]/img")).click();
		order.Size(Size);
		order.Quantity(AnzahlB);
		order.AddtoCart();
		Thread.sleep(1000);
		Assert.assertFalse(driver.findElement(By.id("layer_cart")).isDisplayed());
		test.log(Status.PASS, "Test  passed");
		driver.get(baseUrl);
	}

	@Test(dataProvider = "dp2", priority = 2, enabled = true)
	public void Shopping_Shorts(int Anzahl, String Size, String AnzahlB) throws Exception {
		test = extent.createTest("Shopping_Shorts");
		driver.get(baseUrl);

		driver.findElement(By.xpath("/html/body/div/div[2]/div/div[2]/div/div[1]/ul[1]/li[1]/div/div[1]/div/a[1]/img"))
				.click();

		OrderBox order;

		order = new OrderBox(driver);

		order.Quantity(String.valueOf(Anzahl));
		order.Size(Size);
		order.AddtoCart();
		Thread.sleep(2000);
		Assert.assertTrue(driver.findElement(By.id("layer_cart")).isDisplayed());
		test.log(Status.PASS, "Test  passed");
		driver.get(baseUrl);

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL, result.getThrowable());
		}

		// extent.removeTest(test);
	}

	@DataProvider(name = "dp2")
	public Object[][] readnumericvalue() throws IOException {

		String projectPath = System.getProperty("user.dir");
		String excelPath = (projectPath + "\\src\\test\\resources\\testdata\\LoginDaten.xlsx");
		File src = new File(excelPath);

		FileInputStream fis = new FileInputStream(src);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet1 = wb.getSheet("Shopping");

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
