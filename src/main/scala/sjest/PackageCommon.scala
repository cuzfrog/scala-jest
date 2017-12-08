package sjest

import scala.util.Random

private[sjest] trait PackageCommon {
  private[sjest] implicit class ExRandom(r: Random) {
    def genAlphanumeric(n: Int): String = r.alphanumeric.take(n).mkString
  }

  private[sjest] def NEWLINE: String = NEWLINE()
  private[sjest] def NEWLINE(n: Int = 1): String = System.lineSeparator() * n
}
