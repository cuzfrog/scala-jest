package sjest

import scala.util.Random

abstract class BaseSuite extends utest.TestSuite with AddTestUtilities {

}

trait AddTestUtilities {
  protected implicit final class ExRandom(r: Random) {
    def genAlphanumeric(n: Int): String = r.alphanumeric.take(n).mkString
  }
}
