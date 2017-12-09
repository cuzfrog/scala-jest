package sjest

import sjest.conversion.{ControlType, JsTestContainer}
import sjest.jest.JestApi
import sjest.support.{MutableContext, VisibleForTest}

import scala.collection.mutable
import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@EnableReflectiveInstantiation
abstract class JestSuite extends JestApi {

  import JestSuiteContext.mutableContext

  private[sjest] val container = new JsTestContainer(this.getClass.getName)

  private[sjest] final def setSuiteName(fqcn: String): JsTestContainer = {
    container.setSuiteName(fqcn)
  }

  // ---------- Jest Api -----------
  protected final def describe(description: String)(block: => Unit): Unit = {
    container.enterDescribe(description)
    block
    container.escapeDescribe()
  }
  protected final def test[T](name: String)(block: => T): Unit = {
    container.addTest(name, () => block)
  }
  protected final def beforeEach(block: => Any): Unit = {
    container.addControl(ControlType.BeforeEach, () => block)
  }
  protected final def afterEach(block: => Any): Unit = {
    container.addControl(ControlType.AfterEach, () => block)
  }
  protected final def beforeAll(block: => Any): Unit = {
    container.addControl(ControlType.BeforeAll, () => block)
  }
  protected final def afterAll(block: => Any): Unit = {
    container.addControl(ControlType.AfterAll, () => block)
  }

}

trait GlobalStubAccess[S]{
  self:JestSuite =>
  // ---------- Jest Api (Global stub overloading) -----------
  protected final def test[T](name: String)(block: S => T): Unit = {
    //container.addTest(name, block)
  }
}

private object JestSuiteContext {
  implicit val mutableContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}
}