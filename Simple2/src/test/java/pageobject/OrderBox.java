package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class OrderBox {
	WebDriver driver;

	private By QuantityLocator = By.id("quantity_wanted");

	private By SizeLocator = By.id("group_1");

	private By AddtoCartLocator = By.id("add_to_cart");

	private By AddtoWishlistLocator = By.id("wishlist_button");

	public OrderBox(WebDriver driver) {
		this.driver = driver;
	}

	public void Quantity(String quantity) {
		driver.findElement(QuantityLocator).clear();
		driver.findElement(QuantityLocator).sendKeys(quantity);
	}

	public void Size(String size) {

		Select drpSize = new Select(driver.findElement(SizeLocator));
		drpSize.selectByVisibleText(size);
	}

	public void AddtoCart() {
		driver.findElement(AddtoCartLocator).click();
	}

	public void AddtoWischlist() {
		driver.findElement(AddtoWishlistLocator).click();

	}

}
