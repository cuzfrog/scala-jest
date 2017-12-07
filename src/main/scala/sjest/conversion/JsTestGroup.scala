package sjest.conversion

import sjest.JestSuite
import sjest.support.{MutableContext, Stateful, VisibleForTest}

import scala.collection.mutable

@Stateful
private final class JsTestGroup(val descr: Option[String] = None,
                                val parent: Option[JsTestGroup] = None) extends JsTestTree {
  self =>

  private[this] val subTrees: mutable.ArrayBuffer[JsTestTree] = mutable.ArrayBuffer.empty

  @VisibleForTest
  def getTests: Seq[JsTestCase] = subTrees.flatMap {
    case group: JsTestGroup => group.getTests
    case testCase: JsTestCase => Seq(testCase)
  }

  def addTest[T](name: String, testBlock: () => T)
                (implicit mutableContext: MutableContext[JestSuite]): Unit = {
    if (subTrees.collectFirst { case testCase: JsTestCase if testCase.name == name => testCase }.nonEmpty)
      throw new IllegalArgumentException(s"Duplicated test name: '$name'")
    subTrees += JsTestCase(name, testBlock)
  }

  def addDescribe(description: String)
                 (implicit mutableContext: MutableContext[JestSuite]): JsTestGroup = {
    val child = new JsTestGroup(Some(description), Some(this))
    subTrees += child
    child
  }

  override def clone(): JsTestGroup = {
    val subTreeCopy = self.subTrees.map {
      case group: JsTestGroup => group.clone()
      case testCase: JsTestCase => testCase
    }
    val copy = new JsTestGroup(this.descr, this.parent)
    copy.setTrees(subTreeCopy)
    copy
  }

  private def setTrees(trees: Seq[JsTestTree]): Unit = {
    if (subTrees.nonEmpty)
      throw new IllegalStateException("setTrees can only be used on fresh JsTestGroup")
    subTrees ++ trees
  }
}
