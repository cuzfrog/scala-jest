package sjest

import sjest.support.{MutableContext, Stateful}

import scala.collection.mutable
import scala.scalajs.js

private final case class JsTestCase(name: String, runBlock: js.Function)

@Stateful
private final class JsTestGroup(val descr: Option[String] = None) {
  private[this] val tests: mutable.ArrayBuffer[JsTestCase] = mutable.ArrayBuffer.empty

  def getTests: Seq[JsTestCase] = tests.clone()
  def add[T](name: String, testBlock: () => T)
            (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    if (tests.exists(_.name == name))
      throw new IllegalArgumentException(s"Duplicated test name: '$name'")
    tests += JsTestCase(name, testBlock)
  }
}

@Stateful
private final class JsTestContainer {

  private[this] var suiteName: String = _
  private[this] var currentGroup: JsTestGroup = new JsTestGroup()
  private[this] val gruops: mutable.ArrayBuffer[JsTestGroup] = mutable.ArrayBuffer(currentGroup)

  def add[T](name: String, testBlock: () => T)
            (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    currentGroup.add(name, testBlock)
  }

  def setDescribeGroup(description: Option[String] = None)
                      (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    gruops.find(_.descr == description) match{
      case Some(group) => currentGroup = group
      case None =>
        currentGroup = new JsTestGroup(description)
        gruops += currentGroup
    }
  }

  def getGroups: Seq[JsTestGroup] = gruops.clone()
  def getTests: Seq[JsTestCase] = gruops.flatMap(_.getTests)

  def setSuiteName(name: String)(implicit mutableContext: MutableContext[JestSuite]): this.type = {
    require(name.matches("""[\w-_\.]*\$?"""), s"Bad module name: $name")
    this.suiteName = name
    this
  }

  def getSuiteName: String = {
    checkNameState()
    this.suiteName
  }

  def getFilename: String = {
    checkNameState()
    s"$suiteName.test.js"
  }

  private def checkNameState(): Unit =
    if (suiteName == null) throw new IllegalStateException("Name not set.")
}