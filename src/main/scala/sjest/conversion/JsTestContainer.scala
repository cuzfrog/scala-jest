package sjest.conversion

import sjest.JestSuite
import sjest.support.{MutableContext, Stateful, VisibleForTest}

import scala.collection.mutable

@Stateful
private[sjest] final class JsTestContainer(private[this] var suiteName: String) {

  private[this] val testTree: JsTestGroup = new JsTestGroup(suiteName) //top group
  private[this] var cursor: JsTestGroup = testTree
  private[this] var depth: Int = 0
  private[this] val testControls: mutable.ArrayBuffer[JsControlCase] = mutable.ArrayBuffer.empty

  def addTest[T](name: String, testBlock: () => T)
                (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    cursor.addTest(name, testBlock)
  }

  def addControl[T](tpe: ControlType, block: () => T): Unit = {
    if (testControls.exists(_.tpe == tpe))
      throw new IllegalArgumentException(s"Multiple control '$tpe' defined in $suiteName")
    testControls += JsControlCase(tpe, block)
  }

  def enterDescribe(description: String)
                   (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    cursor = cursor.addDescribe(description)
    depth += 1
  }
  def escapeDescribe()
                    (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    cursor = cursor.parent
      .getOrElse(throw new IllegalStateException("Top test describe cannot be escaped"))
    depth -= 1
  }

  def testContent: String = {
    checkNameState()
    val controls = testControls.map(_.toJsTest(this.suiteName))
    (controls :+ testTree.toJsTest(Seq(this.suiteName))).mkString(NEWLINE)
  }
  private[conversion] def queryTestCase(paths: Seq[String]): JsTestCase = {
    checkDepth()
    testTree.query(paths) match {
      case Some(testCase: JsTestCase) => testCase
      case _ => throw new IllegalArgumentException(s"Cannot query test '${paths.last}' in '$suiteName'")
    }
  }
  private[conversion] def queryControlCase(tpe: ControlType): JsControlCase = {
    testControls.find(_.tpe == tpe).getOrElse(
      throw new IllegalArgumentException(s"No '$tpe' control for '$suiteName'"))
  }

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
  private def checkDepth(): Unit = {
    if (depth != 0) throw new IllegalStateException(s"Illegal describe depth: $depth")
  }

  @VisibleForTest
  private[conversion] def getTests: Seq[JsTestCase] = testTree.getTests
  @VisibleForTest
  private[conversion] def getTree: JsTestGroup = testTree.clone()
}