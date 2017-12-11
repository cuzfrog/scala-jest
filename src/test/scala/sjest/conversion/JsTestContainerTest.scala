package sjest.conversion

import sjest.PropertyTest
import utest._

import scala.scalajs.js
import scala.util.Random

object JsTestContainerTest extends sjest.BaseSuite with PropertyTest {

  import sjest.JestSuiteContext.mutableContext

  val tests = Tests {
    val suiteName = Random.genAlphanumeric(5)
    val impl = new JsTestContainer(suiteName)
    val testName = Random.genAlphanumeric(20)
    "normal-behavior" - {
      assert(impl.getTests.isEmpty)
      val returnValue = Random.genAlphanumeric(40)
      impl.addTest(testName, () => returnValue)
      assert(impl.getTests.size == 1)
      val test = impl.getTests.head
      assert(test.name == testName)
      val runBlock = test.runBlock.asInstanceOf[js.Function0[String]]
      assert(runBlock() == returnValue)

      impl.setSuiteName(suiteName)
      assert(impl.getSuiteName == suiteName)
      assert(impl.getFilename == suiteName + ".test.js")
    }
    "content" - {
      val description = Random.genAlphanumeric(20)
      val controlTypes = ControlType.values.drop(Random.nextInt(ControlType.values.length + 1))

      impl.enterDescribe(description)
      impl.addTest(testName, () => ())
      impl.escapeDescribe()
      impl.setSuiteName(suiteName)
      controlTypes.foreach(impl.addControl(_, () => ()))

      val content = impl.testContent

      val controlCases = controlTypes.map(JsControlCase(_, () => ()))

      val expectedContent = (controlCases.map(_.toJsTest(suiteName)) :+
        s"""describe('$description', () => {
           |test('$testName', () => {
           |  loadTest('${impl.getSuiteName}',['$description','$testName'])();
           |});
           |});""".stripMargin).mkString(NEWLINE)

      assert(content endsWith expectedContent)
    }
    "constraint-behavior" - {
      val containerNullName = new JsTestContainer(null)
      intercept[IllegalStateException] {
        containerNullName.getSuiteName
      }
      intercept[IllegalStateException] {
        containerNullName.getFilename
      }
      intercept[IllegalArgumentException] {
        containerNullName.setSuiteName("bad name contains space")
      }
    }
    "negative-duplicate-names" - {
      impl.addTest("same", () => ())
      intercept[IllegalArgumentException](impl.addTest("same", () => ()))
    }
    "order-retain" - {
      val expectedNames = (0 to Random.nextInt(50)).map { idx => Random.genAlphanumeric(2) + idx }
      expectedNames.foreach(impl.addTest(_, () => ()))

      val resultNames = impl.getTests.map(_.name)
      assert(resultNames == expectedNames)
    }
    "describe-group" - {
      assert(impl.getTests.isEmpty)
      val groupCnt = Random.nextInt(6) + 1
      val expectedTestNames = (0 until groupCnt).flatMap { groupId =>
        val descrOpt = if (groupId == 0) None else Some(Random.genAlphanumeric(10))
        descrOpt.foreach(impl.enterDescribe)
        val testNames = (0 to Random.nextInt(20)).map(_ => Random.genAlphanumeric(20))
        testNames.foreach(name => impl.addTest(name, () => ()))
        descrOpt.foreach(_ => impl.escapeDescribe)
        testNames
      }

      val resultNames = impl.getTests.map(_.name)
      assert(resultNames == expectedTestNames)
    }
    "nested-describe-query" - {
      impl.addTest("t0", () => "v0")
      impl.enterDescribe("d1");
      {
        impl.addTest("t1", () => "v1")
        impl.enterDescribe("d2");
        {
          impl.addTest("t2", () => "v2")
        }
        impl.escapeDescribe()
      }
      impl.escapeDescribe()
      val testCase2 = impl.queryTestCase(Seq("d1", "d2", "t2"))
      val v2 = testCase2.runBlock.apply()
      assert(v2 == "v2")

      val testCase1 = impl.queryTestCase(Seq("d1", "t1"))
      val v1 = testCase1.runBlock.apply()
      assert(v1 == "v1")

      val testCase0 = impl.queryTestCase(Seq("t0"))
      val v0 = testCase0.runBlock.apply()
      assert(v0 == "v0")
    }
    "bad-nested-describe" - {
      impl.enterDescribe("")
      intercept[IllegalStateException](impl.queryTestCase(Seq("")))
    }
    "control-query" - {
      val mockTpe = ControlType.values(Random.nextInt(ControlType.values.length))
      val v = Random.genAlphanumeric(10)
      impl.addControl(mockTpe, () => v)

      val controlCase = impl.queryControlCase(mockTpe)
      val expectedValue = controlCase.runBlock.apply()
      assert(v == expectedValue)

      val nonExistTypes = ControlType.values.filter(_ != mockTpe)
      nonExistTypes.foreach { nonTpe =>
        intercept[IllegalArgumentException](impl.queryControlCase(nonTpe))
      }
    }
  }

  override protected def propertyTestPathExcluded: Seq[String] = Seq(
    "constraint-behavior", "negative-duplicate-names", "nested-describe-query",
    "bad-nested-describe"
  )
}
