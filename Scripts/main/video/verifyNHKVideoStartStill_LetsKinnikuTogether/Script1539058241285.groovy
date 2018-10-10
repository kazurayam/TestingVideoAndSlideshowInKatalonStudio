import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def url = "http://www4.nhk.or.jp/P4975/23/"
def title = "Lets_Kinniku_Together"

Boolean isStill = WebUI.callTestCase(
	findTestCase('Test Cases/main/video/verifyNHKVideoStartStill'),
	[
		'url': url,
		'title': title,
		'waitSeconds': 5
	],
	FailureHandling.OPTIONAL)

println "isStill=${isStill}"

// pass when the Video start still= not in motion, otherwise fail
CustomKeywords.'com.kazurayam.ksbackyard.Assert.assertTrue'(
	"movie ${title} at ${url} is not autoplaying",
	isStill,
	FailureHandling.CONTINUE_ON_FAILURE)




