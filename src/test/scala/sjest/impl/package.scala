package sjest

import scala.util.Random

package object impl {
  implicit class ExRandom(r: Random) {
    def genAlphanumeric(n: Int): String = r.alphanumeric.take(n).mkString
  }
}
