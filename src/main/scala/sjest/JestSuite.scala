package sjest

import sbt.testing.TaskDef
import sjest.jest.JestApi

import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@EnableReflectiveInstantiation
abstract class JestSuite extends JestApi {
  private[sjest] val jsTestContainer = new JsTestContainer

  protected final def test[T](name: String)(block: => T): Unit = {
    jsTestContainer.add(name, () => block)
  }

  protected final def describe(description: String)(tests: => Unit): Unit = {

  }

  private[sjest] final def getTestCase(taskDef: TaskDef): JsTestContainer = {
    jsTestContainer.setName(taskDef.fullyQualifiedName())
  }
}

//private case class DescriptionContext(description: String) extends AnyVal