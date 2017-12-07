package sjest

import scala.collection.mutable
import scala.scalajs.js

private final class JsTestContainer {

  private var name: String = _
  private val tests: mutable.ArrayBuffer[JsTestCase] = mutable.ArrayBuffer.empty

  def add[T](name: String, testBlock: () => T,
             description: Option[String] = None): Unit = {
    if (tests.exists(_.name == name))
      throw new IllegalArgumentException(s"Test description cannot be duplicated: '$name'")
    tests += JsTestCase(name, testBlock, description)
  }

  def getTests: Seq[JsTestCase] = tests.clone()

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

private final case class JsTestCase(name: String,
                                    runBlock: js.Function,
                                    description: Option[String] = None)