package sjest.impl

import sbt.testing.Logger
import sjest.support.ThreadSafe

import scala.collection.mutable.ArrayBuffer

@ThreadSafe
private final class TestLogger extends Logger {
  private val buffer: ArrayBuffer[String] = ArrayBuffer.empty

  override def ansiCodesSupported(): Boolean = false
  override def debug(msg: String): Unit = buffer.synchronized {
    buffer += ("[debug]" + msg)
  }
  override def error(msg: String): Unit = buffer.synchronized {
    buffer += ("[error]" + msg)
  }
  override def warn(msg: String): Unit = buffer.synchronized {
    buffer += ("[warn]" + msg)
  }
  override def info(msg: String): Unit = buffer.synchronized {
    buffer += ("[info ]" + msg)
  }
  override def trace(t: Throwable): Unit = buffer.synchronized {
    val stackTrace = t.getStackTrace.mkString(NEWLINE)
    buffer += ("[trace]" + stackTrace)
  }

  def flush(): Unit = buffer.synchronized {
    buffer.foreach(println)
    buffer.clear()
  }
}
