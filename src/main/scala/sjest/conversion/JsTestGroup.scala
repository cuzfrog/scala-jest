package sjest.conversion

import sjest.JestSuite
import sjest.support.{MutableContext, Stateful, VisibleForTest}

import scala.collection.mutable

@Stateful
private final class JsTestGroup(val descr: Option[String] = None,
                                val parent: Option[JsTestGroup] = None) extends JsTestTree {
  self =>

  private val subTrees: mutable.ArrayBuffer[JsTestTree] = mutable.ArrayBuffer.empty

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

  def query(paths: Seq[String]): Option[JsTestTree] = {
    paths.foldLeft(Option(self: JsTestTree)) { (treeOpt, path) =>
      treeOpt match {
        case Some(group: JsTestGroup) =>
          group.subTrees.find {
            case group: JsTestGroup => group.descr.contains(path)
            case testCase: JsTestCase => testCase.name == path
          }
        case _ => None
      }
    }
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

  @VisibleForTest
  def getTests: Seq[JsTestCase] = subTrees.flatMap {
    case group: JsTestGroup => group.getTests
    case testCase: JsTestCase => Seq(testCase)
  }
}

private object JsTestGroup {
  implicit val conversion: JsTestConversion[JsTestGroup, Seq[String] => String] =
    new JsTestConversion[JsTestGroup, Seq[String] => String] {
      override def convert(t: JsTestGroup): Seq[String] => String = {
        recursiveConvertGroup(t, _)
      }
    }

  private def recursiveConvertGroup(t: JsTestGroup, upperPath: Seq[String]): String = {
    val path = upperPath ++ t.descr
    t.subTrees.map {
      case group: JsTestGroup =>
        val inner = recursiveConvertGroup(group, path)
        wrapDescribe(t.descr, inner)
      case testCase: JsTestCase => testCase.toJsTest(path)
    }.mkString(NEWLINE)
  }

  private def wrapDescribe(descr: Option[String], inner: String): String = {
    if (descr.nonEmpty) {
      s"""describe('${descr.get}', () => {
         |$inner
         |});"""
    }
    else inner
  }
}
