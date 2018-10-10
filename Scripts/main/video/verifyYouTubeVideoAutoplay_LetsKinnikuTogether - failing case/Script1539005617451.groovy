import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def url = "https://www.youtube.com/watch?v=WndOChZSjTk"
def title = "Lets_Kinniku_Together"

Boolean isInMotion = WebUI.callTestCase(
	findTestCase('Test Cases/main/video/verifyYouTubeVideoAutoplay'),
	[
		'url': url,
		'title': title,
		'waitSeconds': 0
	],
	FailureHandling.OPTIONAL)

println "isInMotion=${isInMotion}"

// pass when the Video start still, otherwise fail   ------- this would fail
CustomKeywords.'com.kazurayam.ksbackyard.Assert.assertTrue'(
	"movie ${title} at ${url} is not autoplaying",
	isInMotion,
	FailureHandling.CONTINUE_ON_FAILURE)




