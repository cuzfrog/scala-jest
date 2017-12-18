package sjest

import sjest.conversion.{ControlType, JsTestContainer}
import sjest.jest.{JestGlobal, Matcher, MatcherAsync}
import sjest.support.MutableContext

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSName
import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@EnableReflectiveInstantiation
abstract class JestSuite {

  import MutableContexts.jestSuiteContext

  private[sjest] val container = new JsTestContainer(this.getClass.getName)

  private[sjest] final def setSuiteName(fqcn: String): JsTestContainer = {
    container.setSuiteName(fqcn)
  }

  // ---------- Jest Api -----------
  @noinline
  protected final def describe(description: String)(block: => Unit): Unit = {
    container.enterDescribe(description)
    block
    container.escapeDescribe()
  }
  @noinline
  protected final def test[T](name: String)(block: => T): Unit = {
    container.addTest(name, () => block)
  }
  @noinline
  protected final def beforeEach(block: => Any): Unit = {
    container.addControl(ControlType.BeforeEach, () => block)
  }
  @noinline
  protected final def afterEach(block: => Any): Unit = {
    container.addControl(ControlType.AfterEach, () => block)
  }
  @noinline
  protected final def beforeAll(block: => Any): Unit = {
    container.addControl(ControlType.BeforeAll, () => block)
  }
  @noinline
  protected final def afterAll(block: => Any): Unit = {
    container.addControl(ControlType.AfterAll, () => block)
  }

  @noinline
  protected final def expect[T](value: T): Matcher[T] = JestGlobal.expect(value)

  @noinline
  protected final def expectFunc(func: js.Function): Matcher[js.Function] = JestGlobal.expect(func)

  @noinline
  protected final def expectAsync[T](value: Future[T]): MatcherAsync[T] =
    JestGlobal.expectAsync(value.toJSPromise)

  @noinline
  protected final def assertions(num: Int): js.Object = JestGlobal.expect.assertions(num)
}

//trait GlobalStubAccess[S]{
//  self:JestSuite =>
//  // ---------- Jest Api (Global stub overloading) -----------
//  protected final def test[T](name: String)(block: S => T): Unit = {
//    //container.addTest(name, block)
//  }
//}

private object MutableContexts {
  implicit val jestSuiteContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}
}