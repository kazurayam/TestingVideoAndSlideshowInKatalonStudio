import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def url = "https://www.youtube.com/watch?v=Q80JTXYIteU&feature=youtu.be"
def title = "Katalon Studio - Quick start"
def waitSeconds = 11

Boolean isInMotion = WebUI.callTestCase(
	findTestCase('main/video/YouTubePage_VideoAutoplay'),
	[
		'url': url,
		'title': title,
		'waitSeconds': waitSeconds
	],
	FailureHandling.OPTIONAL)

println "isInMotion=${isInMotion}"

// pass when the Video is in motion, otherwise fail
CustomKeywords.'com.kazurayam.ksbackyard.Assert.assertTrue'(
	"movie ${title} at ${url} is not autoplaying",
	isInMotion,
	FailureHandling.CONTINUE_ON_FAILURE)




