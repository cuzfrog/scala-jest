import sbt.testing.Logger

package object sjest {
  private[sjest] def NEWLINE: String = NEWLINE()
  private[sjest] def NEWLINE(n: Int = 1): String = System.lineSeparator() * n

  private[sjest] implicit class LoggersOps(in: Array[Logger]) {
    def error(msg: String): Unit = in.foreach(_.error(msg))
    def info(msg: String): Unit = in.foreach(_.info(msg))
  }
}
