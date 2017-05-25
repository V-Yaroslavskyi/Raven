import actors.FramesSource
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape, Graph, SourceShape}
import common.Common.Target
import controller.Quadrocopter
import coppelia.{IntW, remoteApi}
import helpers.{Converters, Drawer, TargetFilter}
import drivers.Driver
import org.bytedeco.javacpp.{opencv_core, opencv_imgproc}
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacv.Frame
import org.joda.time.DateTime
import recognition.Detector
import coppelia.remoteApi.simx_opmode_oneshot_wait
import sim.Simulation.vrep


object Main extends App {
  
  implicit val system = ActorSystem("WebcamSource")
  implicit val materializer = ActorMaterializer()
  
  val drawer = Drawer("Camera")
  val detector = Detector(Main.getClass.getClassLoader.getResource("haarcascade_frontalface_default.xml").getPath)
  
  val targetFilter = TargetFilter(70, 10)
  
  val startTime = new DateTime().getMillis.toDouble
  
  val resultSink = Sink.fold[Int, Any](0) {
    case (acc, _) =>
      val time = ((new DateTime().getMillis.toDouble - startTime) / 1000) - 2.5
      println(s"Frames: $acc , Time: $time, avg. FPS: ${acc / time}")
      acc + 1
  }
  
  val vrep = new remoteApi
  vrep.simxFinish(-1)
  val clientID = vrep.simxStart("127.0.0.1", 19997, true, true, 5000, 5)
  println(clientID)
  val handler = new IntW(0)
  val quadrocopter = system.actorOf(Quadrocopter.props(vrep, clientID, handler), "quadrocopter")
  val driver = new Driver(quadrocopter)
  
  vrep.simxGetObjectHandle(clientID, "Quadricopter", handler, simx_opmode_oneshot_wait)
  
  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b =>
    sink =>
      
      import GraphDSL.Implicits._
      
      val source: Graph[SourceShape[Frame], NotUsed] = new FramesSource(Left(-1), Some((vrep, clientID)))
      
      val toMatFlow = Flow[Frame].map(Converters.toGreyMat)
      
      val detectFlow = Flow[Mat].map(x => (x, detector.detect(x)))
      
      val targetFilterFlow = Flow[(Mat, List[Rect])].map { x =>
        println(x._2)
        (x._1, targetFilter.filter(x._2))
      }
      
      val driverFlow = Flow[(Mat, List[Target])].map { x =>
        driver.updatePath(x._2)
        println(x._2)
        x
      }
      
      val displayFlow = Flow[(Mat, List[Target])].map { x => drawer.drawTargets(x._1, x._2) }
      val displayStartFlow = Flow[Frame].map { x => drawer.drawTargets(Converters.toMat(x), List()) }
      
      source ~> toMatFlow ~> detectFlow ~> targetFilterFlow ~> driverFlow ~> displayFlow ~> sink.in
      //    source ~>  displayStartFlow ~> sink.in
      
      ClosedShape
  }).run()
}
