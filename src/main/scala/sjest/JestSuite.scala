package sjest

import sbt.testing.TaskDef
import sjest.jest.JestApi
import sjest.support.MutableContext

import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@EnableReflectiveInstantiation
abstract class JestSuite extends JestApi {
  private[sjest] val jsTestContainer = new JsTestContainer

  protected final def test[T](name: String)(block: => T): Unit = {
    jsTestContainer.add(name, () => block)
  }

  protected final def describe(description: String)(tests: => Unit): Unit = {
    jsTestContainer.setDescribeGroup(Some(description))
    tests
  }

  private[sjest] final def getTestCase(taskDef: TaskDef): JsTestContainer = {
    jsTestContainer.setSuiteName(taskDef.fullyQualifiedName())
  }
}

private object JestSuite {
  implicit val mutableContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}
}