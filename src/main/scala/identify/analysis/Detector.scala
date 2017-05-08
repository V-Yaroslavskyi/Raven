package identify.analysis

import identify.transform.{ Flip, MediaConversion, WithGrey }
import identify.video.Dimensions
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier
import org.bytedeco.javacv.Frame

class Detector(
    val dimensions: Dimensions,
    classifierPath: String,
    scaleFactor: Double = 1.1,
    minNeighbours: Int = 4,
    detectorFlag: HaarDetectorFlag = HaarDetectorFlag.ScaleImage,
    minSize: Dimensions = Dimensions(width = 30, height = 40),
    maxSize: Option[Dimensions] = None
) {

  private val faceCascade = new CascadeClassifier(classifierPath)

  private val minSizeOpenCV = new Size(minSize.width, minSize.height)
  private val maxSizeOpenCV = maxSize.map(d => new Size(d.width, d.height)).getOrElse(new Size())

  /**
   * Given a frame matrix, a series of detected faces
   */
  def detect(orig: Frame): (WithGrey, Seq[DetectObject]) = {
    val mat = MediaConversion.toMat(orig)
    val flipMat = Flip.horizontal(mat)
    val frameMatWithGrey = WithGrey.build(flipMat)

    val currentGreyMat = frameMatWithGrey.grey
    val rects = findFaces(currentGreyMat)
    val detectObjects = for {
      i <- 0L until rects.size()
      rect = rects.get(i)
    } yield DetectObject(i, rect)
    (frameMatWithGrey, detectObjects)
  }

  private def findFaces(greyMat: Mat): RectVector = {
    val rects = new RectVector()
    faceCascade.detectMultiScale(greyMat, rects, scaleFactor, minNeighbours, detectorFlag.flag, minSizeOpenCV, maxSizeOpenCV)
    rects
  }

}