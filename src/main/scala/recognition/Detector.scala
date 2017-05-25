package recognition

import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_objdetect.{ CascadeClassifier, _ }

object Detector {
  def apply(cascadePath: String): Detector = new Detector(cascadePath)
}

class Detector(cascadePath: String) {

  private val faceCascade = new CascadeClassifier(cascadePath)

  private val minSizeOpenCV = new Size(40, 40)
  private val maxSizeOpenCV = new Size()

  def detect(mat: Mat): List[Rect] = {

    val rects = new RectVector()
    faceCascade.detectMultiScale(mat, rects, 1.3, 20, CV_HAAR_SCALE_IMAGE, minSizeOpenCV, maxSizeOpenCV)
    (0L to rects.size()).toList.flatMap{i =>Option(rects.get(i))}
  }

}
