package sjest

import scala.collection.mutable
import scala.scalajs.js

private final class JsTestCase {

  private var name: String = _
  private val tests: mutable.Map[String, js.Function] = mutable.Map.empty

  def add[T](description: String, testBlock: () => T): Unit = {
    if (tests.get(description).isDefined)
      throw new IllegalArgumentException(s"Test description cannot be duplicated: '$description'")
    tests += (description -> testBlock)
  }

  def getPath: String = {
    require(name != null)
    s"target/my-tests/$name.test.js"
  }

  def getTests: Map[String, js.Function] = tests.toMap

  def setName(name: String): this.type = {
    require(name.matches("""[\w-_\.]*\$?"""), s"Bad module name: $name")
    this.name = name
    this
  }

  def getName: String = {
    require(name != null)
    this.name
  }
}
