package actors

/**
 * Created by v-yaroslavskyi on 5/9/17.
 */
object Protocol {
  case object Subscribe
  case class NetworkFrame(keyFrame: Boolean, iw: Int, ih: Int, id: Int, ic: Int, is: Int, image: Array[java.nio.Buffer])

}
