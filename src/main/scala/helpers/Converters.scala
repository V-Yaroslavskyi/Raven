package helpers

import org.bytedeco.javacpp.opencv_core.{ CV_8U, Mat }
import org.bytedeco.javacpp.opencv_imgproc
import org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY
import org.bytedeco.javacv.{ Frame, OpenCVFrameConverter }

/**
 * Created by v-yaroslavskyi on 5/22/17.
 */
object Converters {

  private val converterToMat = new OpenCVFrameConverter.ToMat
  private val converterToImg = new OpenCVFrameConverter.ToIplImage

  def toGreyMat(frame: Frame): Mat = {
    val mat = converterToMat.convert(frame)
    val greyMat = {
      val (rows, cols) = (mat.rows(), mat.cols())
      new Mat(rows, cols, CV_8U)
    }
    opencv_imgproc.cvtColor(mat, greyMat, COLOR_BGR2GRAY, 1)
    greyMat
  }

  def toMat(frame: Frame): Mat = {
    converterToMat.convert(frame)
  }

  def toFrame(mat: Mat): Frame = {
    converterToImg.convert(mat)
  }

}
