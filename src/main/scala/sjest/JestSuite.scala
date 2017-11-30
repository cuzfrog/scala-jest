package sjest

import sbt.testing._

import scala.scalajs.js.annotation._
import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@EnableReflectiveInstantiation
abstract class JestSuite {
  private[sjest] val jsTestCase = new JsTestCase

  protected final def test[T](description: String)(block: => T): Unit = {
    jsTestCase.add(description, () => block)
  }

  private[sjest] final def getTestCase(taskDef: TaskDef): JsTestCase = {
    jsTestCase.setName(taskDef.fullyQualifiedName())
  }
}
