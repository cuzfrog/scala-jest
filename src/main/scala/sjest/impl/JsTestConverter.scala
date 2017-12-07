package sjest.impl

import io.scalajs.nodejs.path.Path
import sjest.nodejs.FSUtils
import sjest.support.Stateless
import sjest.{JsTestContainer, TestFrameworkConfig}

@Stateless
private sealed trait JsTestConverter {
  /**
   * Generate *.test.js file.
   *
   * @return the generated file path.
   */
  def generateJsTest(jsTestContainer: JsTestContainer): String
}

private final class JsTestConverterImpl(implicit config: TestFrameworkConfig)
  extends JsTestConverter {

  def generateJsTest(jsTestContainer: JsTestContainer): String = {

    val module =
      s"""const out = require('${this.resolveOptJsPath}');
         |const loadTest = out.loadTest""".stripMargin

    val contents = genContents(jsTestContainer)

    val content = module + NEWLINE(2) + contents.mkString(NEWLINE)

    val testJsPath = this.resolveTestJsPath(jsTestContainer)
    FSUtils.write(testJsPath, content)
    testJsPath
  }

  private def genContents(jsTestContainer: JsTestContainer): Seq[String] = {
    jsTestContainer.getGroups.filter(_.getTests.nonEmpty).flatMap { group =>
      val descr = group.descr match {
        case Some(dsc) => s"'$dsc'"
        case None => "undefined"
      }
      val tests = group.getTests.map { test =>
        s"""test('${test.name}', () => {
           |  loadTest('${jsTestContainer.getSuiteName}', $descr, '${test.name}')();
           |});""".stripMargin
      }
      wrapDescribe(group.descr, tests)
    }
  }

  private def wrapDescribe(descr: Option[String], tests: Seq[String]): Seq[String] = {
    if (descr.nonEmpty) {
      s"describe('${descr.get}', () => {" +: tests :+ "});"
    }
    else tests
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

