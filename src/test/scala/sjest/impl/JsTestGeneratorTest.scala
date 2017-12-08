package sjest.impl

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import sjest.conversion.JsTestContainer
import sjest.nodejs.FSUtils
import sjest.support.MutableContext
import sjest.{JestSuite, MockObjects, PropertyTest}
import utest._

import scala.util.Random

object JsTestGeneratorTest extends sjest.BaseSuite with PropertyTest {

  override protected def propertyTestRepeatTime: Int = 50

  import MockObjects.mockConfig //implicit config
  private implicit val mutableContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}
  private val impl = ModuleForTest().Prototype.jsTestGenerator

  override def utestAfterEach(path: Seq[String]): Unit = { //tear down
    FSUtils.delete(mockConfig.testJsDir)
  }

  val tests = Tests {
    val testName1 = Random.genAlphanumeric(20)
    val testName2 = Random.genAlphanumeric(20)
    val suiteName = Random.genAlphanumeric(5)
    "test.js-generation" - {
      val mockTestCase = {
        val container = new JsTestContainer(suiteName)
        container.addTest(testName1, () => ())
        container.addTest(testName2, () => println("make some noise"))
        container//.setSuiteName(suiteName)
      }

      val path = impl.generateJsTest(mockTestCase)
      val expectedPath = Path.resolve(mockConfig.testJsDir, mockTestCase.getFilename)
      assert(path == expectedPath)
      assert(Fs.existsSync(path))

      val content = Fs.readFileSync(path).toString()
      val relativeOptJsPath = Path.relative(mockConfig.testJsDir, mockConfig.optJsPath)
      val expectedContent =
        s"""const out = require('$relativeOptJsPath');
           |const loadTest = out.loadTest;
           |const loadControl = out.loadControl;
           |
           |test('$testName1', () => {
           |  loadTest('${mockTestCase.getSuiteName}',['$testName1'])();
           |});
           |test('$testName2', () => {
           |  loadTest('${mockTestCase.getSuiteName}',['$testName2'])();
           |});""".stripMargin
      assert(content.contains(expectedContent))
    }
  }
}
