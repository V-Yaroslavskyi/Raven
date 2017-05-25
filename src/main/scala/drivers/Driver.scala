package drivers

import akka.actor.ActorRef
import common.Common.Target
import controller.Commands.MoveUp

/**
  * Created by v-yaroslavskyi on 5/23/17.
  */
class Driver(quad: ActorRef) {
  
  
  def updatePath(targets: List[Target]) = {
    targets match {
      case Nil =>
        quad ! MoveUp(0.5f)
      case _ =>
        println("target found")
        
    }
  }
  
}
