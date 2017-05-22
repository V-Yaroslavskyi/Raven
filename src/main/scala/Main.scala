import actors.FramesSource
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape, Graph, SourceShape}
import helpers.{Converters, Drawer}
import org.bytedeco.javacpp.{opencv_core, opencv_imgproc}
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacv.Frame
import org.joda.time.DateTime
import recognition.Detector

//object Main extends App {

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel
import org.bytedeco.javacv.CanvasFrame
import org.bytedeco.javacv.FrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgcodecs._
import org.bytedeco.javacpp.opencv_imgproc._

/*
 * Just an example using the opencv to make a colored object tracking,
 * i adpted this code to bytedeco/javacv, i think this will help some people.
 *
 * Waldemar <waldemarnt@outlook.com>
 */

object ColoredObjectTrack {
  
  /**
    * Correct the color range- it depends upon the object, camera quality,
    * environment.
    */

  def main(args: Array[String]): Unit = {
    val cot = new ColoredObjectTrack
    val th = new Thread(cot)
    th.start()
  }
  val rgba_min: opencv_core.CvScalar = cvScalar(0, 0, 130, 0) // RED wide dabur birko
  
  val rgba_max: opencv_core.CvScalar = cvScalar(80, 80, 255, 0)
  
  
}

class ColoredObjectTrack extends Runnable {
  
  
  
  final val INTERVAL = 10 // 1sec
  
  final val CAMERA_NUM = 0 // Default camera for this time
  
  val image = null
  val canvas = new CanvasFrame("Web Cam Live")
  val path = new CanvasFrame("Detection")
  var ii = 0
  val jp = new JPanel
  canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  path.setContentPane(jp)
  
  override def run(): Unit = {
    try {
      val grabber = FrameGrabber.createDefault(CAMERA_NUM)
      val converter = new OpenCVFrameConverter.ToIplImage
      grabber.start()
      var img = null
      var posX = 0
      var posY = 0
      while ( {
        true
      }) {
        val img = converter.convert(grabber.grab)
        if (img != null) { // show image on window
          cvFlip(img, img, 1) // l-r = 90_degrees_steps_anti_clockwise
          
          canvas.showImage(converter.convert(img))
          val detectThrs = getThresholdImage(img)
          val moments = new opencv_imgproc.CvMoments
          cvMoments(detectThrs, moments, 1)
          val mom10 = cvGetSpatialMoment(moments, 1, 0)
          val mom01 = cvGetSpatialMoment(moments, 0, 1)
          val area = cvGetCentralMoment(moments, 0, 0)
          posX = (mom10 / area).toInt
          posY = (mom01 / area).toInt
          // only if its a valid position
          if (posX > 0 && posY > 0) paint(img, posX, posY)
        }
        // Thread.sleep(INTERVAL);
      }
    } catch {
      case e: Exception =>
      
    }
  }
  
  private def paint(img: opencv_core.IplImage, posX: Int, posY: Int) = {
    val g = jp.getGraphics
    path.setSize(img.width, img.height)
    // g.clearRect(0, 0, img.width(), img.height());
    g.setColor(Color.RED)
    // g.fillOval(posX, posY, 20, 20);
    g.drawOval(posX, posY, 20, 20)
    System.out.println(posX + " , " + posY)
  }
  
  private def getThresholdImage(orgImg: opencv_core.IplImage) = {
    val imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1)
    //
    cvInRangeS(orgImg, ColoredObjectTrack.rgba_min, ColoredObjectTrack.rgba_max, imgThreshold) // red
    
    cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0)
    cvSaveImage({
      ii += 1;
      ii
    } + "dsmthreshold.jpg", imgThreshold)
    imgThreshold
  }
  
  def Equalize(bufferedimg: BufferedImage): opencv_core.IplImage = {
    val converter1 = new Java2DFrameConverter
    val converter2 = new OpenCVFrameConverter.ToIplImage
    val iploriginal = converter2.convert(converter1.convert(bufferedimg))
    val srcimg = iploriginal
    val destimg = iploriginal
    cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY)
    cvEqualizeHist(srcimg, destimg)
    destimg
  }
  
  //  }
  
}
