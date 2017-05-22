package helpers

import org.bytedeco.javacpp.helper.opencv_core.AbstractCvScalar
import org.bytedeco.javacpp.opencv_core.{ Mat, Point, Rect, Scalar }
import org.bytedeco.javacpp.opencv_imgproc.{ CV_AA, rectangle }
import org.bytedeco.javacv.CanvasFrame

/**
 * Created by v-yaroslavskyi on 5/22/17.
 */

object Drawer {
  def apply(windowName: String): Drawer = new Drawer(windowName)
}

class Drawer(windowName: String) {

  private val canvas1 = new CanvasFrame(windowName)
  canvas1.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

  def drawRects(mat: Mat, rects: List[Rect]): Unit = {
    for (rect <- rects) {
      rectangle(
        mat,
        new Point(rect.x, rect.y),
        new Point(rect.x + rect.width, rect.y + rect.height),
        new Scalar(AbstractCvScalar.GREEN),
        1,
        CV_AA,
        0
      )
    }

    canvas1.showImage(Converters.toFrame(mat))

  }

}
