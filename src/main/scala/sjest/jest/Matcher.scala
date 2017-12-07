package sjest.jest

import scala.scalajs.js
import scala.scalajs.js.|

@js.native
trait Matcher[T] extends js.Object {

  def toBe(in: T): js.Object = js.native
  def toBeCloseTo(in: T): js.Object = js.native
  def toBeDefined(): js.Object = js.native
  def toBeFalsy(): js.Object = js.native
  def toBeGreaterThan(numb: Double): js.Object = js.native
  def toBeGreaterThanOrEqual(numb: Double): js.Object = js.native
  def toBeLessThan(numb: Double): js.Object = js.native
  def toBeLessThanOrEqual(numb: Double): js.Object = js.native
  def toBeInstanceOf(in: AnyRef): js.Object = js.native
  def toBeNull(): js.Object = js.native
  def toBeTruthy(): js.Object = js.native
  def toBeUndefined(): js.Object = js.native
  def toContain(item: js.Any): js.Object = js.native
  def toContainEqual(item: js.Any): js.Object = js.native
  def toEqual(value: Any): js.Object = js.native
  def toHaveLength(number: Int): js.Object = js.native
  def toMatch(regexpOrString: String | js.RegExp): js.Object = js.native
  def toMatchObject(in: Any): js.Object = js.native
  def toHaveProperty(keyPath: String, value: js.Any = ???): js.Object =
    js.native
  def toMatchSnapshot(optStr: String = ???): js.Object = js.native
  def toThrow(error: Any): js.Object = js.native
  def toThrowErrorMatchingSnapshot(): js.Object = js.native
  val not: Matcher[T] = js.native

}

@js.native
trait MatcherAsync[T] extends Matcher[T] {
  val resolves: Matcher[T] = js.native
  val rejects: Matcher[T] = js.native
}
