package com.kazurayam.ksbackyard

import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.util.Map

import javax.imageio.ImageIO

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor

import com.kazurayam.materials.MaterialRepository
import com.kazurayam.materials.visualtesting.CollectiveXImageDiffer
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.driver.DriverFactory

import internal.GlobalVariable
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.comparison.ImageDiff
import ru.yandex.qatools.ashot.comparison.ImageDiffer
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider
import ru.yandex.qatools.ashot.shooting.ShootingStrategies

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

class ScreenshotDriver {

	static MaterialRepository mr_ = (MaterialRepository)GlobalVariable.MATERIAL_REPOSITORY

	static {
		assert mr_ != null
	}


	/**
	 *
	 * @param profileExpected e.g., 'product'
	 * @param profileAcutual  e.g., 'develop'
	 * @param tSuiteName      e.g., 'TS1'
	 * @param criteriaPercent e.g.,  3.83
	 * @return
	 */
	static def makeDiffs(String profileExpected = 'product', String profileActual = 'develop', String tSuiteName,
			Double criteriaPercent = 3.0) {

		CollectiveXImageDiffer cid = new CollectiveXImageDiffer(mr_)
		cid.makeDiffs(profileExpected, profileActual, tSuiteName, GlobalVariable.CURRENT_TESTCASE_ID, criteriaPercent)
	}




	@Keyword
	static BufferedImage takeElementImage(WebDriver webDriver, WebElement webElement) {
		Screenshot screenshot = new AShot().
				coordsProvider(new WebDriverCoordsProvider()).
				takeScreenshot(webDriver, webElement)
		return screenshot.getImage()
	}

	@Keyword
	static void saveElementImage(WebDriver webDriver, WebElement webElement, Path output) {
		if (!Files.exists(output.getParent())) {
			Files.createDirectories(output.getParent())
		}
		BufferedImage image = takeElementImage(webDriver, webElement)
		ImageIO.write(image, "PNG", output.toFile())
	}




	@Keyword
	static BufferedImage takeEntirePageImage(WebDriver webDriver, Integer timeout = 300) {
		Screenshot screenshot = new AShot().
				shootingStrategy(ShootingStrategies.viewportPasting(timeout)).
				takeScreenshot(webDriver)
		return screenshot.getImage()
	}

	@Keyword
	static void saveEntirePageImage(WebDriver webDriver, File file, Integer timeout = 300) {
		BufferedImage image = takeEntirePageImage(webDriver, timeout)
		ImageIO.write(image, "PNG", file)
	}





	/**
	 * @deprecated use saveEntirePageImage(WebDriver, File, Integer) instead
	 * @param webDriver
	 * @param file
	 */
	@Keyword
	static void takeEntirePage(WebDriver webDriver, File file, Integer timeout = 300) {
		saveEntirePageImage(webDriver, file, timeout)
	}





	/**
	 * return a Map object containing 'evaluated': true when the video is autoplayed,
	 * otherwise false.
	 * 
	 * 1. when the video is loaded, push the playButton so that the vido is stopped
	 * 2. 1st screenshot is taken.
	 * 3. push the playButton so that the video is restarted, wait for some seconds so that the video goes forward
	 * 4. 2nd screenshot is taken.
	 * 5. compare the screenshots. Return true if they are different enough.
	 * 
	 * @param driver WebDriver
	 * @param video  <video> WebElement
	 * @param playButton <button> WebElement to start/stop the video
	 * @param gapTimeSecs 1st screenshot --> gapTimeSecs --> 2nd screenshot  
	 * @return a Map object
	 *         [ 'evaluated'    : Boolean,
	 *           'diffratio'    : Number,
	 *           'diffratiostr' : String,
	 *           'criteria'     : Number,
	 *           'image1'       : BufferedImage,
	 *           'image2'       : BufferedImage,
	 *           'imagediff'    : BufferedImage
	 *         ]
	 */
	@Keyword
	static Map verifyVideoInMotion(
			WebDriver driver,
			WebElement video,
			WebElement playButton,
			Integer gapTimeSecs,
			Double criteriaPercent) {

		//
		Map<String, Object> result = new HashMap<String, Object>()
		result.put('criteria', criteriaPercent)

		// click the start/stop button
		WebUI.executeJavaScript("arguments[0].click()", Arrays.asList(playButton))

		// take the 1st screen shot
		BufferedImage image1 = ScreenshotDriver.takeElementImage(driver, video)
		result.put('image1', image1)

		// again click the start/stop button and wait
		if (gapTimeSecs > 0) {
			WebUI.executeJavaScript("arguments[0].click()", Arrays.asList(playButton))
			WebUI.delay(gapTimeSecs)
		}

		// take the 2nd screenshot
		BufferedImage image2 = ScreenshotDriver.takeElementImage(driver, video)
		result.put('image2', image2)

		// make KSImageDiff
		XImageDiff imgDiff = new XImageDiff(image1, image2)
		result.put('imagediff', imgDiff.getDiffImage())
		result.put('diffratio', imgDiff.getDiffRatio())
		result.put('diffratiostr', imgDiff.getDiffRatioAsString())

		// evaluate result
		if (imgDiff.getDiffRatio() > criteriaPercent) {
			result.put('evaluated', true)
		} else {
			result.put('evaluated', false)
		}

		// return true if the movie autoplay in motion, otherwise false
		return result
	}


	/**
	 * return a Map object containing 'evaluated': true when the video is staying still = is not autoplayed,
	 * otherwise false.
	 * 
	 * 1. when the video is loaded, 1st screenshot is taken.
	 * 2. wait for some seconds
	 * 3. 2nd screenshot is taken.
	 * 4. compare the screenshots. Return true if they have no or just a little difference.
	 * 
	 * @return a Map object
	 *         [ 'evaluated'    : Boolean,
	 *           'diffratio'    : Number,
	 *           'diffratiostr' : String,
	 *           'criteria'     : Number,
	 *           'image1'       : BufferedImage,
	 *           'image2'       : BufferedImage,
	 *           'imagediff'    : BufferedImage
	 *         ]
	 */
	@Keyword
	static Map verifyVideoStartsStill(
			WebDriver driver,
			WebElement video,
			WebElement playButton = null /* not used */,
			Integer gapTimeSecs = 5,
			Double criteriaPercent = 10.0) {

		//
		Map<String, Object> result = new HashMap<String, Object>()
		result.put('criteria', criteriaPercent)

		// take the 1st screen shot
		BufferedImage image1 = ScreenshotDriver.takeElementImage(driver, video)
		result.put('image1', image1)

		// wait for some seconds
		if (gapTimeSecs > 0) {
			WebUI.delay(gapTimeSecs)
		}

		// take the 2nd screenshot
		BufferedImage image2 = ScreenshotDriver.takeElementImage(driver, video)
		result.put('image2', image2)

		// make KSImageDiff
		XImageDiff imgDiff = new XImageDiff(image1, image2)
		result.put('imagediff', imgDiff.getDiffImage())
		result.put('diffratio', imgDiff.getDiffRatio())
		result.put('diffratiostr', imgDiff.getDiffRatioAsString())

		// evaluate result
		if (imgDiff.getDiffRatio() < criteriaPercent) {
			result.put('evaluated', true)
		} else {
			result.put('evaluated', false)
		}

		// return true if the movie start still, otherwise false
		return result
	}


	/**
	 * wraps aShot's ImageDiff object, plus a few getter methods
	 */
	static private class XImageDiff {

		private BufferedImage expectedImage_
		private BufferedImage actualImage_
		private BufferedImage diffImage_
		private Double diffRatio_ = 0.0

		XImageDiff(BufferedImage expected, BufferedImage actual) {
			expectedImage_ = expected
			actualImage_ = actual
			ImageDiff imgDiff = makeDiff(expectedImage_, actualImage_)
			diffRatio_ = diffRatioPercent(imgDiff)
			diffImage_ = imgDiff.getMarkedImage()
		}

		private ImageDiff makeDiff(BufferedImage expected, BufferedImage actual) {
			Screenshot expectedScreenshot = new Screenshot(expected)
			Screenshot actualScreenshot = new Screenshot(actual)
			ImageDiff imgDiff = new ImageDiffer().makeDiff(expectedScreenshot, actualScreenshot)
			return imgDiff
		}

		BufferedImage getExpectedImage() {
			expectedImage_
		}

		BufferedImage getActualImage() {
			actualImage_
		}

		BufferedImage getDiffImage() {
			return diffImage_
		}


		Double getDiffRatio() {
			return diffRatio_
		}

		String getDiffRatioAsString() {
			return String.format('%1$.2f', this.getDiffRatio())
		}


		private Double diffRatioPercent(ImageDiff diff) {
			boolean hasDiff = diff.hasDiff()
			if (!hasDiff) {
				return 0.0
			}
			int diffSize = diff.getDiffSize()
			int area = diff.getMarkedImage().getWidth() * diff.getMarkedImage().getHeight()
			Double diffRatio = diff.getDiffSize() / area * 100
			return diffRatio
		}
	}
}
