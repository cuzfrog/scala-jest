package sjest.jest

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

private[sjest] trait JestApi {
//  protected final def test(name: String)(block:  => Unit): Unit =
//    JestGlobal.test(name,() => block)
//
//  protected final def testAsync(name: String)(block:  => Any): Unit =
//    JestGlobal.test(name,() => block)
//
//  protected final def testOnly(name: String)(block:  => Unit): Unit =
//    JestGlobal.test.only(name,() => block)
//
//  protected final def testSkip(name: String)(block:  => Unit): Unit =
//    JestGlobal.test.skip(name,() => block)
//
//  protected final def testOnlyAsync(name: String)(block:  => Any): Unit =
//    JestGlobal.test.only(name,() => block)

  protected final def expect[T](value: T): Matcher[T] = JestGlobal.expect(value)

  protected final def expectAsync[T](value: Future[T]): MatcherAsync[T] =
    JestGlobal.expectAsync(value.toJSPromise)

  protected final def assertions(num: Int): js.Object = JestGlobal.expect.assertions(num)

  protected final def beforeEach(block: => Any): Unit = JestGlobal.beforeEach(() => block)

  protected final def afterEach(block: => Any): Unit = JestGlobal.afterEach(() => block)

  protected final def beforeAll(block: => Any): Unit = JestGlobal.beforeAll(() => block)

  protected final def afterAll(block: => Any): Unit = JestGlobal.afterAll(() => block)
}