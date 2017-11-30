package sjest

import nodejs.FSUtils

private object JsTestConverter {
  def generateJsTests(jsTestCase: JsTestCase): Unit = {
    val module =
      s"""const out = require('../scala-2.12/simple-jest-test-fastopt.js');
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

    FSUtils.write(jsTestCase.getPath, content)
  }
}

