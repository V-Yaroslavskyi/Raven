import akka.actor.{ Actor, ActorRef, ActorSelection }

class CommandsProcessorActor extends Actor {
  override def receive = {
    case "START" =>
      println("started")
  }
}