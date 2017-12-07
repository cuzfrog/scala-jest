package sjest.conversion

import scala.scalajs.js

private final case class JsTestCase(name: String, runBlock: js.Function) extends JsTestTree

private object JsTestCase {

}
