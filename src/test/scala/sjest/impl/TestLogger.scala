package sjest.impl

import sbt.testing.Logger

import scala.collection.mutable.ArrayBuffer

private final class TestLogger extends Logger {
  private val buffer: ArrayBuffer[String] = ArrayBuffer.empty

  override def ansiCodesSupported(): Boolean = false
  override def debug(msg: String): Unit = buffer += ("[debug]" + msg)
  override def error(msg: String): Unit = buffer += ("[error]" + msg)
  override def warn(msg: String): Unit = buffer += ("[warn]" + msg)
  override def info(msg: String): Unit = buffer += ("[info ]" + msg)
  override def trace(t: Throwable): Unit = {
    val stackTrace = t.getStackTrace.mkString(NEWLINE)
    buffer += ("[trace]" + stackTrace)
  }

  def flush(): Unit = {
    buffer.foreach(println)
    buffer.clear()
  }
}
