package sjest.conversion

import scala.scalajs.js

private[conversion] case class JsControlCase(tpe: ControlType, runBlock: js.Function0[_])

private object JsControlCase {
  implicit val conversion: Convertible[JsControlCase, String => String] =
    new Convertible[JsControlCase, String => String] {
      override def convert(t: JsControlCase): String => String = {
        val convertFromPath = (fqcn: String) => {
          s"""${t.tpe.decapitalizedString}(() => {
             |  loadControl('$fqcn','${t.tpe}')();
             |});""".stripMargin
        }
        convertFromPath
      }
    }
}