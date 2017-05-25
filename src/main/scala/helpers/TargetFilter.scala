package helpers

import com.sun.scenario.animation.SplineInterpolator
import common.Common.Target
import org.bytedeco.javacpp.opencv_core.Rect


import scala.collection.mutable

class TargetFilter(maxVelocity: Int, initComboCount: Int) {
  
  private val targets = mutable.Map[Int, (Int, (Int, Int))]()
  
  private var comboCount: Double = initComboCount
  
  
  
  def filter(rects: List[Rect]): List[Target] = {
    
    val targs = for {
      rect <- rects
      res <- {
        val center = (rect.x + rect.width() / 2, rect.y + rect.height() / 2)
        val distances = targets.mapValues(t => (t._1, math.pow(t._2._1 - center._1, 2) + math.pow(t._2._2 - center._2, 2)))
        distances.filter(_._2._2 < math.pow(maxVelocity, 2)).toList match {
          case Nil =>
            targets.update(targets.size, (1, center))
            None
          case lst =>
            val closest = lst.minBy(_._2._2)
            Some((closest._1, center, closest._2._2, closest._2._1))
          
        }
      }
      
    } yield res
    
    targs.groupBy(_._1).map(x => (x._2.minBy(_._3), x._2.length)).flatMap{x =>
      targets.update(x._1._1, (x._1._4 + x._2, x._1._2))
      comboCount += 0.05
      println(x)
      if (x._1._4 > comboCount)
        Some(Target(x._1._1, x._1._2._1, x._1._2._2))
      else
        None
    }.toList
  }
}

object TargetFilter {
  def apply(maxVelocity: Int, comboCount: Int): TargetFilter = new TargetFilter(maxVelocity, comboCount)
}
