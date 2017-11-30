package sjest.jest

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

private trait BaseSuite {
  def test(name: String)(block:  => Unit): Unit =
    JestGlobal.test(name,() => block)

  def testAsync(name: String)(block:  => Any): Unit =
    JestGlobal.test(name,() => block)

  def testOnly(name: String)(block:  => Unit): Unit =
    JestGlobal.test.only(name,() => block)

  def testSkip(name: String)(block:  => Unit): Unit =
    JestGlobal.test.skip(name,() => block)

  def testOnlyAsync(name: String)(block:  => Any): Unit =
    JestGlobal.test.only(name,() => block)

  def expect[T](value: T): Matcher[T] = JestGlobal.expect(value)

  def expectAsync[T](value: Future[T]): MatcherAsync[T] =
    JestGlobal.expectAsync(value.toJSPromise)

  def assertions(num: Int): js.Object = JestGlobal.expect.assertions(num)

  def beforeEach(block: => Any): Unit = JestGlobal.beforeEach(() => block)

  def afterEach(block: => Any): Unit = JestGlobal.afterEach(() => block)

  def beforeAll(block: => Any): Unit = JestGlobal.beforeAll(() => block)

  def afterAll(block: => Any): Unit = JestGlobal.afterAll(() => block)
}