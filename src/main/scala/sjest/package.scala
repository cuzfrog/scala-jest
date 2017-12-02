import scala.util.Random

package object sjest {
  private[sjest] implicit class ExR(r: Random) {
    def genAlphanumeric(n: Int): String = r.alphanumeric.take(n).mkString
  }

  private[sjest] def NEWLINE: String = NEWLINE()
  private[sjest] def NEWLINE(n: Int = 1): String = System.lineSeparator() * n
}
