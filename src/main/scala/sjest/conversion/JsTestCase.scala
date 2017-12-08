package sjest.conversion

import scala.scalajs.js

private final case class JsTestCase(name: String, runBlock: js.Function0[_]) extends JsTestTree

private object JsTestCase {
  implicit val conversion: Convertible[JsTestCase, Seq[String] => String] =
    new Convertible[JsTestCase, Seq[String] => String] {
      override def convert(t: JsTestCase): Seq[String] => String = {
        val convertFromPath = (upperPath: Seq[String]) => {
          val (fqcn :: path) = upperPath
          val jsPath = "['" + (path :+ t.name).mkString("','") + "']"

          s"""test('${t.name}', () => {
             |  loadTest('$fqcn',$jsPath)();
             |});""".stripMargin
        }
        convertFromPath
      }
    }
}
