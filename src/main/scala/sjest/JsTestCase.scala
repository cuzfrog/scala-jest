package sjest

import scala.collection.mutable
import scala.scalajs.js

private[sjest] final class JsTestCase {

  private var name: String = _
  private val tests: mutable.ArrayBuffer[(String, js.Function)] = mutable.ArrayBuffer.empty

  def add[T](description: String, testBlock: () => T): Unit = {
    if (tests.exists { case (descr, _) => descr == description })
      throw new IllegalArgumentException(s"Test description cannot be duplicated: '$description'")
    tests += (description -> testBlock)
  }

  def getTests: Seq[(String, js.Function)] = tests.toList

  def setName(name: String): this.type = {
    require(name.matches("""[\w-_\.]*\$?"""), s"Bad module name: $name")
    this.name = name
    this
  }

  def getName: String = {
    checkNameState()
    this.name
  }

  def getFilename: String = {
    checkNameState()
    s"$name.test.js"
  }

  private def checkNameState(): Unit =
    if (name == null) throw new IllegalStateException("Name not set.")
}
