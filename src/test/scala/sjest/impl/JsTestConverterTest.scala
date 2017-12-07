package sjest.impl

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import sjest.{JestSuite, JsTestContainer, MockObjects, PropertyTest}
import sjest.nodejs.FSUtils
import sjest.support.MutableContext
import utest._

import scala.util.Random

object JsTestConverterTest extends TestSuite with PropertyTest {

  override protected def propertyTestRepeatTime: Int = 50

  import MockObjects.mockConfig //implicit config
  private implicit val mutableContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}
  private val impl = ModuleForTest().Prototype.jsTestConverter

  override def utestAfterEach(path: Seq[String]): Unit = { //tear down
    FSUtils.delete(mockConfig.testJsDir)
  }

  val tests = Tests {
    val testName1 = Random.genAlphanumeric(20)
    val testName2 = Random.genAlphanumeric(20)
    "test.js-generation" - {
      val mockTestCase = {
        val jsTestContainer = new JsTestContainer
        jsTestContainer.add(testName1, () => ())
        jsTestContainer.add(testName2, () => println("make some noise"))
        val randomName = Random.genAlphanumeric(5)
        jsTestContainer.setSuiteName(randomName)
      }

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
           |test('$testName1', () => {
           |  loadTest('${mockTestCase.getSuiteName}', undefined, '$testName1')();
           |});
           |test('$testName2', () => {
           |  loadTest('${mockTestCase.getSuiteName}', undefined, '$testName2')();
           |});""".stripMargin
      assert(content.contains(expectedContent))
    }
    "test-nested-in-describe" - {
      val description = Random.genAlphanumeric(20)
      val mockTestCase = {
        val jsTestContainer = new JsTestContainer
        jsTestContainer.enterDescribe(description)
        jsTestContainer.add(testName1, () => ())
        jsTestContainer.add(testName2, () => println("make some noise"))
        jsTestContainer.escapeDescribe(description)
        val randomName = Random.genAlphanumeric(5)
        jsTestContainer.setSuiteName(randomName)
      }
      val path = impl.generateJsTest(mockTestCase)
      val content = Fs.readFileSync(path).toString()
      val expectedContent =
        s"""describe('$description', () => {
           |test('$testName1', () => {
           |  loadTest('${mockTestCase.getSuiteName}', '$description', '$testName1')();
           |});
           |test('$testName2', () => {
           |  loadTest('${mockTestCase.getSuiteName}', '$description', '$testName2')();
           |});
           |});""".stripMargin

      assert(content endsWith expectedContent)
    }
  }
}
