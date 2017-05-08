package distance

import org.bytedeco.javacpp.opencv_core.Rect

object Metrics {

  val imageWidth = 640
  val imageHeight = 480

  def coords(rect1: Rect, rect2: Rect): SphereCoords = {

    val disp = disparity(rect1, rect2)
    val cartesianCoords = cartCoords(rect1, disp)

    val distance = pxToCm(disp)
    val yawDeg = yaw(cartesianCoords)
    val pitchDeg = pitch(cartesianCoords)

    println(s"disparity: $disp")
    println(s"distance: $distance cm")
    println(s"yaw: $yawDeg deg")
    println(s"pitch $pitchDeg deg")

    SphereCoords(distance, yawDeg, pitchDeg)
  }

  private def pxToCm(disparity: Int) = {
    val d = 23.311491
    val a = 700842320
    val c = 1.7511255
    val b = 4.0332697

    d + (a - d) / (1 + math.pow(disparity / c, b))
  }

  private def disparity(rect1: Rect, rect2: Rect): Int = {

    val rightCenter = rect1.x + rect1.width / 2
    val leftCenter = rect2.x + rect2.width / 2

    (rightCenter - leftCenter).abs
  }

  private def cartCoords(rect: Rect, disparity: Double): CartCoords = {
    val f = 300
    val xCenter = rect.x + rect.width / 2
    val yCenter = rect.y + rect.height / 2

    val Z = f / disparity

    val X = {
      val x = xCenter - imageWidth / 2
      Z * x / f
    }

    val Y = {
      val y = yCenter - imageHeight / 2
      Z * y / f
    }

    CartCoords(X, Y, Z)
  }

  private def yaw(point: CartCoords): Double =
    math.atan2(point.z, point.x).toDegrees - 90

  private def pitch(point: CartCoords): Double =
    -math.atan2(
      point.y,
      math.sqrt(point.x * point.x + point.z * point.z)
    ).toDegrees
}
