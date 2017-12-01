package sjest

import scala.collection.mutable
import scala.collection.immutable
import scala.scalajs.js

private final class JsTestCase {

  private var name: String = _
  private val tests: mutable.Map[String, js.Function] = mutable.SortedMap.empty

  def add[T](description: String, testBlock: () => T): Unit = {
    if (tests.get(description).isDefined)
      throw new IllegalArgumentException(s"Test description cannot be duplicated: '$description'")
    tests += (description -> testBlock)
  }

  def getTests: Map[String, js.Function] = immutable.SortedMap(tests.toSeq.reverse: _*)

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
