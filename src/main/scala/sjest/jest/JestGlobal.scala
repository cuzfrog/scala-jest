package sjest.jest

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSGlobalScope, JSName}
import scala.scalajs.js.|

@JSGlobalScope
@js.native
private[sjest] object JestGlobal extends js.Object {

  def test(str: String, function: js.Function0[Any]): Unit = js.native

  def describe(str: String, function: js.Function0[Any]): Unit = js.native

  def afterEach(function: js.Function0[Any]): Unit = js.native

  def beforeEach(function: js.Function0[Any]): Unit = js.native

  def beforeAll(function: js.Function0[Any]): Unit = js.native

  def afterAll(function: js.Function0[Any]): Unit = js.native

  val test: JestTestObject = js.native

  val describe: JestTestObject = js.native

  def expect[T](in: T): Matcher[T] = js.native

  @JSName("expect")
  def expectAsync[T](in: js.Promise[T]): MatcherAsync[T] = js.native

  val expect: ExpectObject = js.native
}


@js.native
sealed trait ExpectObject extends js.Object {
  def extend(matchers: js.Object): js.Any = js.native
  def assertions(in: Int): js.Object = js.native
  def hasAssertions(): js.Object = js.native
  def objectContaining(obj: js.Object): js.Object = js.native
  def stringContaining(str: String): js.Object = js.native
  def stringMatching(regexp: js.RegExp): js.Object = js.native
  def addSnapshotSerializer(serializer: js.Any): js.Object = js.native
}

@js.native
sealed trait JestTestObject extends js.Object {
  def only(str: String, function: js.Function0[_]): Unit = js.native
  def skip(str: String, function: js.Function0[_]): Unit = js.native
}
