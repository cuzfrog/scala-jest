package sjest.impl

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import sjest.{JsTestContainer, MockObjects}
import sjest.nodejs.FSUtils
import utest._

import scala.util.Random

object JsTestConverterTest extends TestSuite {
  private val descripton1 = Random.genAlphanumeric(20)
  private val descripton2 = Random.genAlphanumeric(20)

  private val mockTestCase = {
    val jsTestContainer = new JsTestContainer
    jsTestContainer.add(descripton1, () => ())
    jsTestContainer.add(descripton2, () => println("make some noise"))
    val randomName = Random.genAlphanumeric(5)
    jsTestContainer.setName(randomName)
  }

  import MockObjects.mockConfig //implicit config

  private val impl = ModuleForTest().Prototype.jsTestConverter

  override def utestAfterAll(): Unit = { //tear down
    FSUtils.delete(mockConfig.testJsDir)
  }

  val tests = Tests {
    "test.js-generation" - {
      val path = impl.generateJsTest(mockTestCase)
      val expectedPath = Path.resolve(mockConfig.testJsDir, mockTestCase.getFilename)
      assert(path == expectedPath)
      assert(Fs.existsSync(path))

      val content = Fs.readFileSync(path).toString()
      val relativeOptJsPath = Path.relative(mockConfig.testJsDir, mockConfig.optJsPath)
      val expectedContent =
        s"""const out = require('$relativeOptJsPath');
           |const loadTest = out.loadTest
           |
           |test('$descripton1', () => {
           |  loadTest('${mockTestCase.getName}','$descripton1')();
           |});
           |test('$descripton2', () => {
           |  loadTest('${mockTestCase.getName}','$descripton2')();
           |});""".stripMargin
      assert(content.contains(expectedContent))
    }
  }
}
