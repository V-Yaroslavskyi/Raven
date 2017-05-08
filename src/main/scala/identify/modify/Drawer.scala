package identify.modify

import identify.analysis.DetectObject
import identify.transform.WithGrey
import org.bytedeco.javacpp.helper.opencv_core.AbstractCvScalar
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgproc._

class Drawer(fontScale: Float = 0.6f) {

  private val RedColour = new Scalar(AbstractCvScalar.RED)

  /**
   * Clones the Mat, draws squares around the faces on it using the provided [[DetectObject]] sequence and returns the new Mat
   */
  def drawFaces(withGrey: WithGrey, faces: Seq[DetectObject]): Mat = {
    val clonedMat = withGrey.orig.clone()
    for (f <- faces) drawFace(clonedMat, f)
    clonedMat
  }

  private def drawFace(clonedMat: Mat, f: DetectObject): Unit = {
    rectangle(
      clonedMat,
      new Point(f.rect.x, f.rect.y),
      new Point(f.rect.x + f.rect.width, f.rect.y + f.rect.height),
      RedColour,
      1,
      CV_AA,
      0
    )

    // draw the face number
    val cvPoint = new Point(f.rect.x, f.rect.y - 20)
    putText(clonedMat, s"Balloon ${f.id}", cvPoint, FONT_HERSHEY_SIMPLEX, fontScale, RedColour)
  }

}
