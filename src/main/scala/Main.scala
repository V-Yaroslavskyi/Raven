import javax.swing.JFrame

import actors.VideoSenderActor
import actors.VideoSenderActor.Finish
import akka.actor.{ ActorSystem, Props }
import akka.stream._
import akka.stream.scaladsl.{ Sink, _ }
import identify.analysis.Detector
import distance.{ Metrics, SphereCoords }
import identify.transform.{ Flip, MediaConversion, WithGrey }
import identify.video.{ Dimensions, Webcam }
import identify.modify.Drawer
import org.bytedeco.javacpp.opencv_core.Rect
import org.bytedeco.javacv.{ CanvasFrame, FFmpegFrameGrabber }

import scala.language.postfixOps

object Main extends App {
  //  implicit val system = ActorSystem("WebcamSource")
  //  implicit val materializer = ActorMaterializer()
  //
  //  val path = Main.getClass.getClassLoader.getResource("bright_cascade15.xml").getPath
  //  val imageDimensions = Dimensions(Metrics.imageWidth, Metrics.imageHeight)
  //
  //  val leftWebCamSource = Webcam.source(deviceId = 0, imageDimensions)
  //
  //  val left = leftWebCamSource
  //
  //  val videoSenderActor = system.actorOf(Props[VideoSenderActor], "videoSender")
  //
  //  val g = RunnableGraph.fromGraph(GraphDSL.create(Sink.actorRef(videoSenderActor, Finish)) { implicit b => sink =>
  //
  //    import GraphDSL.Implicits._
  //
  //    left ~> sink.in
  //
  //    ClosedShape
  //  }).run()
  val canvas1 = new CanvasFrame("Left")
  canvas1.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

  //  val cap = new FFmpegFrameGrabber("http://10.0.1.49:8090/test.swf")
  val cap = new FFmpegFrameGrabber("http://localhost:8080/test.mpg")
  cap.start()

  show

  def show: Unit = {
    Thread.sleep(300)
    val img = cap.grabImage()
    println(img.imageHeight)
    canvas1.showImage(img)
    show
  }
}
