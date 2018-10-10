package com.kazurayam.materials.visualtesting

import java.nio.file.Path
import java.util.stream.Collectors

import javax.imageio.ImageIO

import com.kazurayam.ksbackyard.ScreenshotDriver.XImageDiff
import com.kazurayam.materials.FileType
import com.kazurayam.materials.Material
import com.kazurayam.materials.MaterialPair
import com.kazurayam.materials.MaterialRepository

public class CollectiveXImageDiffer {

	private MaterialRepository mr_
	private VTListener listener_

	CollectiveXImageDiffer(MaterialRepository mr) {
		mr_ = mr
	}

	void setVTListener(VTListener listener) {
		listener_ = listener
	}

	/**
	 *
	 * @param profileExpected e.g., 'product'
	 * @param profileAcutual  e.g., 'develop'
	 * @param tSuiteName      e.g., 'TS1'
	 * @param criteriaPercent e.g.,  3.83
	 * @return
	 */
	def makeDiffs(String profileExpected = 'product',
			String profileActual = 'develop',
			String tSuiteName,
			String tCaseName,
			Double criteriaPercent = 3.0) {

		if (tSuiteName == null) {
			throw new IllegalArgumentException('tSuiteName is required')
		}
		if (tCaseName == null) {
			throw new IllegalArgumentException('tCaseName is required')
		}

		List<MaterialPair> materialPairs =
				mr_.getRecentMaterialPairs(profileExpected, profileActual, tSuiteName).
				stream().filter { mp ->
					mp.getLeft().getFileType() == FileType.PNG
				}.collect(Collectors.toList())
		if (materialPairs.size() > 0) {
			listener_.fatal(">>> materialPairs.size() is 0")
		}

		Statistics stats = new Statistics()

		for (MaterialPair pair : materialPairs) {
			Material expMate = pair.getExpected()
			Material actMate = pair.getActual()
			XImageDiff diff = new XImageDiff(
					ImageIO.read(expMate.getPath().toFile()),
					ImageIO.read(actMate.getPath().toFile()))

			stats.add(diff)

			String fileName = resolveImageDiffFilename(profileExpected,
					profileActual,
					expMate,
					actMate,
					diff,
					criteriaPercent)

			Path pngFile = mr_.resolveMaterialPath(tCaseName,
					expMate.getDirpathRelativeToTSuiteResult(),fileName)

			ImageIO.write(diff.getDiffImage(), "PNG", pngFile.toFile())

			// verify the diffRatio, fail the test if the ratio is greater than criteria
			if (diff.getDiffRatio() > criteriaPercent && listener_ != null) {
				listener_.failed(">>> diffRatio = ${diff.getDiffRatio()} is exceeding criteria = ${criteriaPercent}")
			}

		}

		// show statistics
		listener_.info(">>> #makeDiffs ${stats.toString()}")
		listener_.info(">>> #makeDiffs average of diffRatios is ${String.format('%.2f', stats.diffRatioAverage())}")
		listener_.info(">>> #makeDiffs standard deviation of diffRatio is ${String.format('%.2f', stats.evalStandardDeviation())}")
		listener_.info(">>> #makeDiffs recommended criteria is ${String.format('%.2f', stats.evalRecommendedCriteria(1.6))}")
	}



	/**
	 * 
	 * @return
	 */
	String resolveImageDiffFilename(String profileExpected,
			String profileActual,
			Material expMate,
			Material actMate,
			XImageDiff diff,
			Double criteriaPercent) {
		//
		String fileName = expMate.getPath().getFileName().toString()
		String fileId = fileName.substring(0, fileName.lastIndexOf('.'))
		String expTimestamp = expMate.getParent().getParent().getTSuiteTimestamp().format()
		String actTimestamp = actMate.getParent().getParent().getTSuiteTimestamp().format()
		Boolean failed = (diff.getDiffRatio() > criteriaPercent)
		//
		StringBuilder sb = new StringBuilder()
		sb.append("${fileId}.")
		sb.append("${expTimestamp}_${profileExpected}")
		sb.append("-")
		sb.append("${actTimestamp}_${profileActual}")
		sb.append(".")
		sb.append("(${String.format('%.2f', diff.getDiffRatio())})")
		sb.append("${(failed) ? 'FAILED' : ''}")
		sb.append(".png")
		return sb.toString()
	}




	/**
	 *
	 */
	class Statistics {

		private List<XImageDiff> list_

		Statistics() {
			list_ = new ArrayList<XImageDiff>()
		}

		void add(XImageDiff diff) {
			list_.add(diff)
		}

		String toString() {
			StringBuilder sb = new StringBuilder()
			sb.append(">>> # diffRatio: ")
			sb.append("[")
			def count = 0
			for (XImageDiff diff : list_) {
				if (count > 0) {
					sb.append(", ")
				}
				sb.append(String.format('%.2f', diff.getDiffRatio()))
				count += 1
			}
			sb.append("] percent")
			return sb.toString()
		}

		Double diffRatioAverage() {
			Double sum = 0.0
			for (XImageDiff diff : list_) {
				sum += diff.getDiffRatio()
			}
			return sum / list_.size()
		}

		Double evalStandardDeviation() {
			Double average = this.diffRatioAverage()
			Double s = 0.0
			for (XImageDiff diff : list_) {
				s += (average - diff.getDiffRatio()) * (average - diff.getDiffRatio())
			}
			return Math.sqrt(s / list_.size)
		}

		Double evalRecommendedCriteria(Double factor = 1.5) {
			Double average = this.diffRatioAverage()
			Double stddevi = this.evalStandardDeviation()
			return average + stddevi * factor
		}
	}
}
