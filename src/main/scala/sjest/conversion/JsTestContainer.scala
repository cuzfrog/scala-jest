package sjest.conversion

import sjest.JestSuite
import sjest.support.{MutableContext, Stateful, VisibleForTest}

@Stateful
private[sjest] final class JsTestContainer {

  private[this] var suiteName: String = _
  private[this] val testTree: JsTestGroup = new JsTestGroup //top group
  private[this] var cursor: JsTestGroup = testTree

  def add[T](name: String, testBlock: () => T)
            (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    cursor.addTest(name, testBlock)
  }

  def enterDescribe(description: String)
                   (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    cursor = cursor.addDescribe(description)
  }
  def escapeDescribe(description: String)
                    (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    if (!cursor.descr.contains(description))
      throw new IllegalStateException(s"Current describ is not '$description'")
    cursor = cursor.parent
      .getOrElse(throw new IllegalStateException("Top test describe cannot be escaped"))
  }

  def testContent: String = testTree.toJsTest()
  private[conversion] def queryTestCase(paths: Seq[String]): JsTestCase = {
    checkNameState()
    testTree.query(paths) match{
      case Some(testCase:JsTestCase) => testCase
      case _ => throw new IllegalArgumentException(s"Cannot query test '${paths.last}' in '$suiteName'")
    }
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

  @VisibleForTest
  private[conversion] def getTests: Seq[JsTestCase] = testTree.getTests
  @VisibleForTest
  private[conversion] def getTree: JsTestGroup = testTree.clone()
}