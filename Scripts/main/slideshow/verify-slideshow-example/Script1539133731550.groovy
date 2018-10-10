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

// ensure output directory
Path tmpDir = Paths.get(RunConfiguration.getProjectDir()).resolve('tmp/slideshow')
Files.createDirectories(tmpDir)

// open browser
WebUI.openBrowser('')
WebDriver driver = DriverFactory.getWebDriver()


WebUI.navigateToUrl('https://www.mandel.org.uk/')

WebUI.waitForPageLoad(5)

TestObject bannerTO = findTestObject('Page - Mandelbrot Explorer Home/div_banner')

// make sure the banner is present
WebUI.verifyElementPresent(bannerTO, 10, FailureHandling.STOP_ON_FAILURE)

// grasp the banner as a WebElement
WebElement banner = driver.findElement(By.xpath(bannerTO.findPropertyValue('xpath')))

// take the starter screenshot of the banner
BufferedImage img1 = CustomKeywords.'com.kazurayam.ksbackyard.ScreenshotDriver.takeElementImage'(
	driver, banner)
Path out1 = tmpDir.resolve("${title}_1.png")
ImageIO.write(img1, "PNG", out1.toFile())


WebUI.closeBrowser()