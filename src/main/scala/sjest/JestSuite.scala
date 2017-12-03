package sjest

import sbt.testing.TaskDef
import sjest.jest.JestApi

import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@EnableReflectiveInstantiation
abstract class JestSuite extends JestApi{
  private[sjest] val jsTestCase = new JsTestCase

  protected final def test[T](description: String)(block: => T): Unit = {
    jsTestCase.add(description, () => block)
  }

  private[sjest] final def getTestCase(taskDef: TaskDef): JsTestCase = {
    jsTestCase.setName(taskDef.fullyQualifiedName())
  }
}
