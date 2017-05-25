package actors

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import org.bytedeco.javacv.{CanvasFrame, FFmpegFrameGrabber, Frame, FrameGrabber}
import akka.NotUsed
import akka.actor.{ActorLogging, ActorSystem, DeadLetterSuppression, Props}
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import akka.stream.scaladsl.Source
import coppelia.{CharWA, IntW, IntWA, remoteApi}
import coppelia.remoteApi._
import helpers.Converters
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgcodecs
import org.bytedeco.javacv.FrameGrabber.ImageMode
import sim.Simulation.{clientID, image, resolution, vrep, _}


class FramesSource(frameSource: Either[Int, String], vrepOpt: Option[(remoteApi, Int)]) extends GraphStage[SourceShape[Frame]] {
  
  
  val out: Outlet[Frame] = Outlet("FramesSource")
  override val shape: SourceShape[Frame] = SourceShape(out)
  
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      
      val getFrame: () => Frame = frameSource match {
        case Left(-1) =>
          val vrep = vrepOpt.get._1
          val clientID = vrepOpt.get._2
          val camera1Handler = new IntW(0)
          val image = new CharWA(0)
          val resolution = new IntWA(0)
          vrep.simxGetObjectHandle(clientID, "CopterCamera", camera1Handler, simx_opmode_oneshot_wait)
          vrep.simxGetVisionSensorImage(clientID, camera1Handler.getValue, resolution, image, 0, simx_opmode_oneshot_wait)
          println(image.getLength)
          () => {
            vrep.simxGetVisionSensorImage(clientID, camera1Handler.getValue, resolution, image, 0, simx_opmode_streaming)
            image.saveImg1()
            Thread.sleep(50)
            Converters.toFrame(opencv_imgcodecs.imread("/Users/v-yaroslavskyi/Study/Raven/left.jpg"))
          }
          
        case Left(deviceId) =>
          val g: FrameGrabber = FrameGrabber.createDefault(deviceId)
          g.setImageWidth(640)
          g.setImageHeight(480)
          g.setBitsPerPixel(CV_8U)
          g.setImageMode(ImageMode.COLOR)
          g.start()
          println(s"Started successfully from camera $deviceId")
          () => g.grab()
        
        case Right(streamUrl) =>
          val cap = new FFmpegFrameGrabber(streamUrl)
          cap.start()
          println(s"Started successfully from stream $streamUrl")
          () => cap.grabImage()
      }
      
      
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          push(out, getFrame())
        }
      })
    }
  }
  
}
