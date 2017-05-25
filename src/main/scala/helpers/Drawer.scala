package helpers

import common.Common.Target
import org.bytedeco.javacpp.helper.opencv_core.AbstractCvScalar
import org.bytedeco.javacpp.opencv_core
import org.bytedeco.javacpp.opencv_core.{Mat, Point, Rect, Scalar}
import org.bytedeco.javacpp.opencv_imgproc.{CV_AA, line, putText}
import org.bytedeco.javacv.CanvasFrame


object Drawer {
  def apply(windowName: String): Drawer = new Drawer(windowName)
}

class Drawer(windowName: String) {

  private val canvas1 = new CanvasFrame(windowName)
  canvas1.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

  def drawTargets(mat: Mat, tars: List[Target]): Unit = {
    for (t <- tars) {
      line(
        mat,
        new Point(t.x - 20, t.y),
        new Point(t.x + 20, t.y),
        new Scalar(AbstractCvScalar.GREEN),
        1,
        CV_AA,
        0
      )
      line(
        mat,
        new Point(t.x, t.y - 20),
        new Point(t.x, t.y + 20),
        new Scalar(AbstractCvScalar.GREEN),
        1,
        CV_AA,
        0
      )
      putText(
        mat,
        s"target${t.num}",
        new Point(t.x, t.y - 25),
        opencv_core.FONT_HERSHEY_PLAIN,
        1.0,
        new Scalar(AbstractCvScalar.GREEN)
      )
      
    }

    canvas1.showImage(Converters.toFrame(mat))

  }

}
