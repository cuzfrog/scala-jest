package sjest

import io.scalajs.nodejs.fs.Fs
import sjest.nodejs.FSUtils
import utest._

import scala.util.Random

object JsTestConverterTest extends TestSuite {

  private val mockTestCase = {
    val jsTestCase = new JsTestCase
    jsTestCase.add("I am test1", () => ())
    jsTestCase.add("I am test2", () => println("make some noise"))
    val randomName = Random.alphanumeric.take(5).mkString
    jsTestCase.setName(randomName)
  }

  private implicit val mockConfig: TestFrameworkConfig =
    TestFrameworkConfig(
      optJsPath = "/tmp/dir1/fastopt.js",
      testJsDir = "/tmp/jests/",
      nodejsCmdOfPath = _ => JestFramework.NodejsCmd("npm", Array("test")),
      autoRunTestInSbt = true
    )

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
      val expectedContent =
        s"""const out = require('../dir1/fastopt.js');
           |const loadTest = out.loadTest
           |
           |test('I am test2', () => {
           |  loadTest('${mockTestCase.getName}','I am test2')();
           |});
           |test('I am test1', () => {
           |  loadTest('${mockTestCase.getName}','I am test1')();
           |});""".stripMargin
      assert(content.contains(expectedContent))
    }
  }
}
