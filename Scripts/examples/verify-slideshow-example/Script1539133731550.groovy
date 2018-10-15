import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javax.imageio.ImageIO

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import com.kazurayam.ksbackyard.ScreenshotDriver.ImageDifference
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/*
 * verify-slideshow-example
 */

String url = 'https://www.mandel.org.uk/'
String title = 'Mandelbrot Explorer'
int intervalSeconds = 6
int slideCount = 4

// ensure output directory
Path tmpDir = Paths.get(RunConfiguration.getProjectDir()).resolve('tmp/slideshow')
Files.createDirectories(tmpDir)

// open browser
//WebUI.openBrowser('')
CustomKeywords.'oneoff.KazurayamSpecifics.openBrowser'()

WebUI.setViewPortSize(1024,768)
WebDriver driver = DriverFactory.getWebDriver()

WebUI.navigateToUrl(url)

WebUI.delay(3)

TestObject bannerTO = findTestObject('Page - Mandelbrot Explorer Home/div_banner')

// make sure the banner is present
WebUI.verifyElementPresent(bannerTO, 10, FailureHandling.STOP_ON_FAILURE)

// grasp the banner as a WebElement
WebElement banner = driver.findElement(By.xpath(bannerTO.findPropertyValue('xpath')))

// take screenshots of slides and save it to disk
List<BufferedImage> imageList = new ArrayList<BufferedImage>()
for (int i = 0; i < slideCount; i++) {
	BufferedImage img = CustomKeywords.'com.kazurayam.ksbackyard.ScreenshotDriver.takeElementImage'(
		driver, banner)
	Path out = tmpDir.resolve("${title}_${i}.png")
	ImageIO.write(img, "PNG", out.toFile())
	imageList.add(img)
	// wait for the slide to change
	WebUI.delay(intervalSeconds)
}

// check differences of the image pairs:
//     (img0,img1), (img1,img2), (img2,img3) ... (imgN-1, imgN), (imgN, img0)
// save the diff-images to disk, 
List<ImageDifference> diffList = new ArrayList<ImageDifference>()
for (int i = 0; i < slideCount; i++) {
	int x = (i < slideCount - 1) ? (i + 1) : 0
	ImageDifference difference =
		CustomKeywords.'com.kazurayam.ksbackyard.ScreenshotDriver.verifyImages'(
			imageList.get(i), imageList.get(x), 90.0)
	diffList.add(difference)
	Path out = tmpDir.resolve("${title}_diff_${i}x${x}(${difference.getRatioAsString()}).png")
	ImageIO.write(difference.getDiffImage(), "PNG", out.toFile())
	
	
	CustomKeywords.'com.kazurayam.ksbackyard.Assert.assertTrue'(
		"diff_${i}x${x} looks unchanged",
		difference.imagesAreDifferent())
}

WebUI.closeBrowser()