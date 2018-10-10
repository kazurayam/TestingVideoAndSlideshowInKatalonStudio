import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.nio.file.Path

import javax.imageio.ImageIO

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import com.kazurayam.materials.MaterialRepository
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject

import internal.GlobalVariable as GlobalVariable

assert url != null
assert title != null
assert waitSeconds >= 0
Double criteriaPercent = 90.0

MaterialRepository mr = (MaterialRepository)GlobalVariable.MATERIAL_REPOSITORY
assert mr != null

WebUI.openBrowser('')
WebUI.setViewPortSize(1024, 768)
WebDriver driver = DriverFactory.getWebDriver()

// start loading the YouTube page
WebUI.navigateToUrl(url)  // video will be started on load

// wait for the YouTube page to load
WebUI.verifyElementPresent(
	findTestObject('Page - NHK/article'),
	10, FailureHandling.STOP_ON_FAILURE)

// switch to iframe0
driver.switchTo().defaultContent()
driver.switchTo().frame(0)

// top <video> element in the NHK page
WebElement mainVideo = driver.findElement(
	By.xpath(findTestObject('Page - NHK/video0').findPropertyValue('xpath')))

// top <button> element to stop/play video in the YouTube page
WebElement playButton = driver.findElement(
	By.xpath(findTestObject('Page - NHK/button_big-play-button0').findPropertyValue('xpath')))


// verify if the YouTube Vido is autoplaying or not
/* result is a Map object
 *         [ 'evaluated': Boolean,
 *           'diffratio': Number,
 *           'criteria' : Number,
 *           'image1'   : BufferedImage,
 *           'image2'   : BufferedImage,
 *           'imagediff': BufferedImage
 *         ]
 */
Map<String, Object> result =
	CustomKeywords.'com.kazurayam.ksbackyard.ScreenshotDriver.verifyVideoStartsStill'(
		driver, mainVideo)

// write the screenshot taken at the start
Path png1 = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_1st.png")
ImageIO.write(result.get('image1'), "PNG", png1.toFile())

// write the screenshot taken after waitSeconds
Path png2 = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_2nd.png")
ImageIO.write(result.get('image2'), "PNG", png2.toFile())

// write the imageDiff between the above 2 screenshots
String descriptor = "(${String.format('%1$.2f', result.get('diffratio'))})${result.get('evaluated')?'':'FAILED'}"
Path pngDiff = mr.resolveMaterialPath(GlobalVariable.CURRENT_TESTCASE_ID, "${title}_diff${descriptor}.png")
ImageIO.write(result.get('imagediff'), "PNG", pngDiff.toFile())

WebUI.closeBrowser()

println "['url':${url}, 'title':'${title}','result':['evaluated':${result.evaluated}, 'diffratio':${result.diffratio}," +
	" 'criteria':${result.criteria}]]"

// return boolean if the movie autoplaying or not
return (Boolean)result.get('evaluated')