package sjest

import nodejs.{FSUtils, Path}

private object JsTestConverter {
  def generateJsTests(jsTestCase: JsTestCase)(implicit config: TestFrameworkConfig): Unit = {
    val module =
      s"""const out = require('${this.resolveOptJsPath}');
         |const loadTest = out.loadTest
         |
      """.stripMargin
    val contents = jsTestCase.getTests.map { case (description, _) =>
      s"""test('$description', () => {
         |  loadTest('${jsTestCase.getName}','$description')();
         |});
         |
      """.stripMargin
    }
    val NEWLINE = System.lineSeparator()
    val content = module + NEWLINE + contents.mkString

    FSUtils.write(this.resolveTestJsPath(jsTestCase), content)
  }

  private def resolveOptJsPath(implicit config: TestFrameworkConfig): String = ???

  private def resolveTestJsPath(jsTestCase: JsTestCase)
                               (implicit config: TestFrameworkConfig): String = {
    Path.resolve(config.testJsDir, jsTestCase.getFilename)
  }
}

