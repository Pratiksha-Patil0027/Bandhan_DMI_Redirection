package com.dmiurl.pageobjects;

	import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
 
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
 

 
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
 
    //  Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }
    //  Click element with wait
    public void clickElement(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public WebElement waitForElement(By locator) {
    return new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.visibilityOfElementLocated(locator));
}
 
    //  Send text with wait
    public void sendText(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }
 
    //  Get text with wait
    public String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }
 
    //  Wait for visibility
    public void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
 
    //  Wait for invisibility
    public void waitForInvisibility(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }
 
    //  Wait for visibility by locator
    public void waitForVisibilityOfLocated(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
 
    // Wait for invisibility by locator
    public void waitForInvisibilityOfLocated(By by) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }
 
    //  Click from list by exact text match
    public void clickElementFromListByText(List<WebElement> elements, String text) {
        for (WebElement element : elements) {
            if (element.getText().trim().equalsIgnoreCase(text.trim())) {
                clickElement(element);
                return;
            }
        }
        throw new RuntimeException("No element with text '" + text + "' found.");
    }
 
    //  Click from list by partial text match
    public void clickElementFromListByPartialText(List<WebElement> elements, String partialText) {
        for (WebElement element : elements) {
            if (element.getText().contains(partialText)) {
                clickElement(element);
                return;
            }
        }
        throw new RuntimeException("No element containing text '" + partialText + "' found.");
    }
 
    //  Window Handling (for hybrid apps or webviews)
    public void switchToMainWindow() {
        Set<String> windows = driver.getWindowHandles();
        if (!windows.isEmpty()) {
            driver.switchTo().window(windows.iterator().next());
        } else {
            throw new RuntimeException("No window found to switch.");
        }
    }
 
    public void switchToChildWindow() {
        Set<String> windows = driver.getWindowHandles();
        Iterator<String> it = windows.iterator();
        if (windows.size() < 2) {
            throw new RuntimeException("Less than two windows open.");
        }
        it.next(); // Skip main
        driver.switchTo().window(it.next());
    }
 
    public void switchToWindowByIndex(int index) {
        Set<String> windows = driver.getWindowHandles();
        if (index < 0 || index >= windows.size()) {
            throw new IllegalArgumentException("Invalid window index: " + index);
        }
        Iterator<String> it = windows.iterator();
        for (int i = 0; i <= index; i++) {
            String windowHandle = it.next();
            if (i == index) {
                driver.switchTo().window(windowHandle);
            }
        }
    }

}
