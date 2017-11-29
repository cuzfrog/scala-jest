package sjest

import nodejs.FSUtils

private object JsTestConverter {
  def generateJsTests(jsTestCase: JsTestCase): Unit = {
    val module =
      s"""const out = require('../../js-tests/target/scala-2.12/js-tests-test-fastopt.js');
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

