package sjest

import nodejs.{FSUtils, Path}

private object JsTestConverter {
  /**
   * Generate *.test.js file.
   *
   * @return the generated file path.
   */
  def generateJsTest(jsTestCase: JsTestCase)
                    (implicit config: TestFrameworkConfig): String = {

    val module =
      s"""const out = require('${this.resolveOptJsPath}');
         |const loadTest = out.loadTest""".stripMargin
    val contents = jsTestCase.getTests.map { case (description, _) =>
      s"""test('$description', () => {
         |  loadTest('${jsTestCase.getName}','$description')();
         |});""".stripMargin
    }



    val content = module + NEWLINE(2) + contents.mkString(NEWLINE)

    val testJsPath = this.resolveTestJsPath(jsTestCase)
    FSUtils.write(testJsPath, content)
    testJsPath
  }

  private def resolveOptJsPath(implicit config: TestFrameworkConfig): String = {
    //todo: check if optJs is stale
    Path.relative(config.testJsDir, config.optJsPath)
  }

  private def resolveTestJsPath(jsTestCase: JsTestCase)
                               (implicit config: TestFrameworkConfig): String = {
    Path.resolve(config.testJsDir, jsTestCase.getFilename)
  }

  private def NEWLINE(implicit n: Int = 1) = System.lineSeparator() * n
}

