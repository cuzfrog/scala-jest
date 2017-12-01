import scala.util.Random

package object sjest {
  implicit class ExR(r: Random) {
    def genAlphanumeric(n: Int): String = r.alphanumeric.take(n).mkString
  }
}
