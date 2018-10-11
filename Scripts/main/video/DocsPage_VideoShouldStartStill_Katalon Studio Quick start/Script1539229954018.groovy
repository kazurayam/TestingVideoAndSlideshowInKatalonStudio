import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/*
 * KatalonDocsPage_VideoStartStill_Quick start 
 */

def url = "https://docs.katalon.com/katalon-studio/tutorials/quick_start.html"
def title = "Katalon Docs - Quick start"
def waitSeconds = 5

Boolean isStayingStill = WebUI.callTestCase(
	findTestCase('main/video/DocsPage_VideoShouldStartStill'),
	[
		'url': url,
		'title': title,
		'waitSeconds': waitSeconds
	],
	FailureHandling.OPTIONAL)

println "isStayingStill=${isStayingStill}"

// pass when the Video is in motion, otherwise fail
CustomKeywords.'com.kazurayam.ksbackyard.Assert.assertTrue'(
	"movie ${title} at ${url} is not staying still",
	isStayingStill,
	FailureHandling.CONTINUE_ON_FAILURE)