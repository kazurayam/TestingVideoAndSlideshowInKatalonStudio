import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.nio.file.Path

import javax.imageio.ImageIO

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import com.kazurayam.materials.MaterialRepository
import com.kazurayam.ksbackyard.ScreenshotDriver.ImageDifference
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject

import internal.GlobalVariable as GlobalVariable

/*
 * KatalonDocsPage_VideoStartStill 
 */

assert url != null
assert title != null
assert waitSeconds >= 0
Double criteriaPercent = 90.0

MaterialRepository mr = (MaterialRepository)GlobalVariable.MATERIAL_REPOSITORY
assert mr != null

WebUI.openBrowser('')
WebDriver driver = DriverFactory.getWebDriver()

// start loading the Katalon Docs page
WebUI.navigateToUrl(url)  // video will be started on load

// wait for the Katalon Docs Quick start page to load
WebUI.verifyElementPresent(
	findTestObject('Page - Katalon Quick start/iframe'),
	10, FailureHandling.STOP_ON_FAILURE)

// switch to iframe
WebUI.switchToFrame(findTestObject('Page - Katalon Quick start/iframe'),
	10, FailureHandling.STOP_ON_FAILURE)

// top <video> element in the NHK page
TestObject videoTestObject = 
	findTestObject('Page - Katalon Quick start/video_main')
WebUI.verifyElementPresent(videoTestObject, 10, FailureHandling.STOP_ON_FAILURE)
WebElement mainVideo = driver.findElement(
	By.xpath(videoTestObject.findPropertyValue('xpath')))

// top <button> element to stop/play video in the YouTube page
TestObject playButtonTestObject = 
	findTestObject('Page - Katalon Quick start/button_ytp-play-button')
WebUI.verifyElementPresent(playButtonTestObject, 10, FailureHandling.STOP_ON_FAILURE)
WebElement playButton = driver.findElement(
	By.xpath(playButtonTestObject.findPropertyValue('xpath')))

WebUI.delay(3)

// verify if the YouTube Vido started still
ImageDifference difference =
	CustomKeywords.'com.kazurayam.ksbackyard.ScreenshotDriver.verifyVideoStartsStill'(
		driver, mainVideo)

// write the screenshot taken at the start
Path png1 = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_1st.png")
ImageIO.write(difference.getExpectedImage(), "PNG", png1.toFile())

// write the screenshot taken after waitSeconds
Path png2 = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_2nd.png")
ImageIO.write(difference.getActualImage(), "PNG", png2.toFile())

// write the imageDiff between the above 2 screenshots
String descriptor = "(${difference.getRatioAsString()})${difference.imagesAreSimilar()?'':'FAILED'}"
Path pngDiff = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_diff${descriptor}.png")
ImageIO.write(difference.getDiffImage(), "PNG", pngDiff.toFile())

WebUI.closeBrowser()

println "['url':${url}, 'title':'${title}','difference.getEvaluated()':${difference.imagesAreSimilar()}" +
	", 'difference.getRatio()':${difference.getRatio()}" +
	", 'difference.getCriteria()':${difference.getCriteria()}]]"

// return boolean if the movie autoplaying or not
return difference.imagesAreSimilar()
