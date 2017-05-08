import javax.swing.JFrame
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{ Sink, _ }

import identify.analysis.Detector
import distance.{ Metrics, SphereCoords }
import identify.transform.{ Flip, MediaConversion, WithGrey }
import identify.video.{ Dimensions, Webcam }
import identify.modify.Drawer

import org.bytedeco.javacpp.opencv_core.Rect
import org.bytedeco.javacv.CanvasFrame

import scala.language.postfixOps

object Main extends App {
  implicit val system = ActorSystem("WebcamSource")
  implicit val materializer = ActorMaterializer()

  val path = Main.getClass.getClassLoader.getResource("bright_cascade15.xml").getPath
  val imageDimensions = Dimensions(Metrics.imageWidth, Metrics.imageHeight)
  val detector1 = new Detector(dimensions = imageDimensions, classifierPath = path)
  val detector2 = new Detector(dimensions = imageDimensions, classifierPath = path)

  val canvas1 = new CanvasFrame("Left")
  canvas1.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

  val canvas2 = new CanvasFrame("Right")
  canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

  val leftWebCamSource = Webcam.source(deviceId = 0, imageDimensions)
  val rightWebCamSource = Webcam.source(deviceId = 1, imageDimensions)

  val drawer = new Drawer()

  val left = leftWebCamSource
    .map(detector1.detect)
    .filter(tuple => {
      val mat = drawer.drawFaces(tuple._1, tuple._2)
      canvas1.showImage(MediaConversion.toFrame(mat))
      tuple._2.nonEmpty
    })
    .map(tuple => tuple._2.head.rect)

  val right = rightWebCamSource
    .map(detector1.detect)
    .filter(tuple => {
      val mat = drawer.drawFaces(tuple._1, tuple._2)
      canvas2.showImage(MediaConversion.toFrame(mat))
      tuple._2.nonEmpty
    })
    .map(tuple => tuple._2.head.rect)

  val disparityZipper = GraphDSL.create() { implicit b =>
    val zip = b.add(ZipWith[Rect, Rect, SphereCoords](Metrics.coords))
    UniformFanInShape(zip.out, zip.in0, zip.in1)
  }

  val resultSink = Sink.ignore

  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b => sink =>

    import GraphDSL.Implicits._
    val zipper = b.add(disparityZipper)

    left ~> zipper.in(0)
    right ~> zipper.in(1)
    zipper.out ~> sink.in

    ClosedShape
  }).run()

}
