package sjest

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import sjest.nodejs.FSUtils
import utest._

import scala.util.Random

object JsTestConverterTest extends TestSuite {

  private val descripton1 = Random.genAlphanumeric(20)
  private val descripton2 = Random.genAlphanumeric(20)

  private val mockTestCase = {
    val jsTestCase = new JsTestCase
    jsTestCase.add(descripton1, () => ())
    jsTestCase.add(descripton2, () => println("make some noise"))
    val randomName = Random.genAlphanumeric(5)
    jsTestCase.setName(randomName)
  }

  import MockObjects.mockConfig

  override def utestAfterAll(): Unit = { //tear down
    FSUtils.delete(mockConfig.testJsDir)
  }

  val tests = Tests {
    "test.js-generation" - {
      val path = JsTestConverter.generateJsTest(mockTestCase)
      val expectedPath = mockConfig.testJsDir + mockTestCase.getFilename
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
