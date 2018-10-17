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

import internal.GlobalVariable as GlobalVariable

assert url != null
assert title != null
assert waitSeconds >= 0
Double criteriaPercent = 90.0

MaterialRepository mr = (MaterialRepository)GlobalVariable.MATERIAL_REPOSITORY
assert mr != null

WebUI.openBrowser('')
//CustomKeywords.'oneoff.KazurayamSpecifics.openBrowser'()

WebUI.setViewPortSize(1024, 768)
WebDriver driver = DriverFactory.getWebDriver()

// start loading the YouTube page
WebUI.navigateToUrl(url)  // video will be started on load

// wait for the YouTube page to load
WebUI.verifyElementPresent(
	findTestObject('Page - YouTube/button_ytp-play-button'),
	10, FailureHandling.STOP_ON_FAILURE)

// <video> element in the YouTube page
WebElement mainVideo = driver.findElement(By.cssSelector("video.html5-main-video"))

// <button> element to stop/play video in the YouTube page
WebElement playButton = driver.findElement(By.cssSelector("button.ytp-play-button"))

// verify if the YouTube Vido is autoplaying or not
ImageDifference difference = 
	CustomKeywords.'com.kazurayam.ksbackyard.YouTubeVideoVerifier.verifyVideoInMotion'(
		driver, mainVideo, playButton,
		waitSeconds, criteriaPercent)

// write the screenshot taken at the start
Path png1 = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_1st.png")
ImageIO.write(difference.getExpectedImage(), "PNG", png1.toFile())

// write the screenshot taken after waitSeconds
Path png2 = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_2nd.png")
ImageIO.write(difference.getActualImage(), "PNG", png2.toFile())

// write the imageDiff between the above 2 screenshots
String descriptor = "(${difference.getRatioAsString()})${difference.imagesAreDifferent()?'':'FAILED'}"
Path pngDiff = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_diff${descriptor}.png")
ImageIO.write(difference.getDiffImage(), "PNG", pngDiff.toFile())

WebUI.closeBrowser()

println "['url':${url}, 'title':'${title}','difference.getEvaluated()':${difference.imagesAreDifferent()}" +
	", 'difference.getRatio()':${difference.getRatio()}" +
	", 'difference.getCriteria()':${difference.getCriteria()}]]"

// return boolean if the movie autoplaying or not
return difference.imagesAreDifferent()
