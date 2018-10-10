import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javax.imageio.ImageIO

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

String url = 'https://www.mandel.org.uk/'
String title = 'Mandelbrot Explorer'
int intervalSeconds = 6
int slideCount = 4

// ensure output directory
Path tmpDir = Paths.get(RunConfiguration.getProjectDir()).resolve('tmp/slideshow')
Files.createDirectories(tmpDir)

// open browser
WebUI.openBrowser('')
WebDriver driver = DriverFactory.getWebDriver()

WebUI.navigateToUrl(url)

TestObject bannerTO = findTestObject('Page - Mandelbrot Explorer Home/div_banner')

// make sure the banner is present
WebUI.verifyElementPresent(bannerTO, 10, FailureHandling.STOP_ON_FAILURE)

// grasp the banner as a WebElement
WebElement banner = driver.findElement(By.xpath(bannerTO.findPropertyValue('xpath')))

// take screenshots of slides in the banner
for (int i = 1; i <= slideCount; i++) {
	BufferedImage img = CustomKeywords.'com.kazurayam.ksbackyard.ScreenshotDriver.takeElementImage'(
		driver, banner)
	Path out1 = tmpDir.resolve("${title}_${i}.png")
	ImageIO.write(img, "PNG", out1.toFile())
	// wait for the slide change
	WebUI.delay(intervalSeconds)
}

WebUI.closeBrowser()