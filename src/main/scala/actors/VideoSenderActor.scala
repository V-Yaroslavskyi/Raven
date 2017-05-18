package actors

import java.awt.Image
import java.awt.image.BufferedImage

import actors.Protocol.{ NetworkFrame, Subscribe }
import akka.actor.{ Actor, ActorRef, ActorSelection }
import org.bytedeco.javacv.{ CanvasFrame, Frame }

object VideoSenderActor {
  case object Finish

}

class VideoSenderActor extends Actor {

  import VideoSenderActor._
  val canvas1 = new CanvasFrame("Left")
  canvas1.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

  private var subscriber: Option[ActorRef] = None

  override def receive: Receive = {

    case Subscribe =>
      subscriber = Some(sender())

    case frame: Frame =>
      canvas1.showImage(frame)
    //      println(frame.image.head.asInstanceOf[java.nio.DirectByteBuffer])
    //      val data = NetworkFrame(frame.keyFrame, frame.imageWidth, frame.imageHeight, frame.imageDepth, frame.imageChannels, frame.imageStride, frame.image)
    //      subscriber.foreach(_ ! canvas1.getCanvas.)

    case Finish =>
      println("finish")
  }

}
