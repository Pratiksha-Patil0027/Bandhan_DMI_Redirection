package com.dmiurl.pageobjects;


import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

	@FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div/button")
	private WebElement disclaimer_OkButton_Element;

	@FindBy(xpath = "//*[@id=\"dropdownMenuButton5\"]/span/i")
	private WebElement languageDropdown_Element;

	@FindBy(xpath = "//*[@id=\"root\"]/div[2]/nav/div/ul/li[1]/a/div/div/div/button")
	private List<WebElement> languageDropValue_Elements;

	@FindBy(xpath = "//*[@id=\"root\"]/div[2]/nav/div/ul/li[2]/a")
	private WebElement aboutUs_Element;

	@FindBy(xpath = "//*[@id=\"root\"]/div[2]/nav/div/ul/li[3]/a")
	private WebElement ourServices_Element;

	@FindBy(xpath = "//*[@id=\"root\"]/div[2]/nav/div/ul/li[4]/a")
	private WebElement ourExperience_Element;

	  public HomePage(WebDriver driver) {
        super(driver);      //  Pass driver to BasePage
    }


	
	public void clickOn_Disclaimer_OkButton() {
		try{
    // Wait for button and click
        WebElement disclaimerButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'btn-secondary')]")));
        disclaimerButton.click();
        System.out.println("Clicked Disclaimer OK");
		} catch (Exception e) {
        System.out.println("Disclaimer popup NOT visible → Skipping it");
    }
}



	public void clickOn_LanguageDropdown() {
		clickElement(languageDropdown_Element);
	}

	public void clickOn_LanguageDropdown(String language) {
		for(WebElement languageOption : languageDropValue_Elements) {
			String langText = languageOption.getText().trim();
			if(langText.equals(language)) {
				clickElement(languageOption);
				break;
			}
		}
	}
		public void clickOn_AboutUs() {
		clickElement(aboutUs_Element);
	}

	public void clickOn_OurServices() {
		clickElement(ourServices_Element);
	}

	public void clickOn_OurExperience() {
		clickElement(ourExperience_Element);
	}

}
	


