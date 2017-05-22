package actors

import akka.stream.{ Attributes, Outlet, SourceShape }
import akka.stream.stage.{ GraphStage, GraphStageLogic, OutHandler }
import org.bytedeco.javacv.{ CanvasFrame, FFmpegFrameGrabber, Frame, FrameGrabber }
import akka.NotUsed
import akka.actor.{ ActorLogging, ActorSystem, DeadLetterSuppression, Props }
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{ Cancel, Request }
import akka.stream.scaladsl.Source
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacv.FrameGrabber.ImageMode

/**
 * Created by v-yaroslavskyi on 5/8/17.
 */

class FramesSource(streamUrl: String, sourceId: Int) extends GraphStage[SourceShape[Frame]] {

  val out: Outlet[Frame] = Outlet("FramesSource")
  override val shape: SourceShape[Frame] = SourceShape(out)
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      //      val cap = new FFmpegFrameGrabber(streamUrl)
      //      cap.start()

      val g: FrameGrabber = FrameGrabber.createDefault(0)
      g.setImageWidth(640)
      g.setImageHeight(480)
      g.setBitsPerPixel(CV_8U)
      g.setImageMode(ImageMode.COLOR)
      g.start()

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          sourceId match {
            //            case 0 =>
            //              push(out, cap.grabImage())
            case 1 =>
              push(out, g.grab())
          }
        }
      })
    }
  }

}
