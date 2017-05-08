package actors

import akka.actor.Actor

/**
  * Created by v-yaroslavskyi on 5/8/17.
  */
class VideoProcessorActor extends Actor{
  override def receive: Receive = {
    case x => println(x)
  }
}
