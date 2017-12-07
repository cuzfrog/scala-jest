package sjest.impl

import io.scalajs.nodejs.path.Path
import sjest.TestFrameworkConfig
import sjest.conversion.JsTestContainer
import sjest.nodejs.FSUtils
import sjest.support.{SideEffect, Stateless}
import sjest.NEWLINE

@Stateless
private sealed trait JsTestGenerator {
  /**
   * Generate *.test.js file.
   *
   * @return the generated file path.
   */
  @SideEffect("IO")
  def generateJsTest(jsTestContainer: JsTestContainer): String
}

private final class JsTestGeneratorImpl(implicit config: TestFrameworkConfig)
  extends JsTestGenerator {

  def generateJsTest(jsTestContainer: JsTestContainer): String = {
    val module =
      s"""const out = require('${this.resolveOptJsPath}');
         |const loadTest = out.loadTest""".stripMargin

    val content = module + NEWLINE(2) + jsTestContainer.testContent

    val testJsPath = this.resolveTestJsPath(jsTestContainer)
    FSUtils.write(testJsPath, content)
    testJsPath
  }

  private def resolveOptJsPath(implicit config: TestFrameworkConfig): String = {
    //todo: check if optJs is stale
    Path.relative(config.testJsDir, config.optJsPath)
  }

  private def resolveTestJsPath(jsTestContainer: JsTestContainer)
                               (implicit config: TestFrameworkConfig): String = {
    Path.resolve(config.testJsDir, jsTestContainer.getFilename)
  }
}

