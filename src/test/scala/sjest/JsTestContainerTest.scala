package sjest

import utest._

import scala.util.Random
import sjest.impl.ExRandom
import sjest.support.MutableContext

import scala.scalajs.js

object JsTestContainerTest extends TestSuite with PropertyTest {

  private implicit val mutableContext: MutableContext[JestSuite] = new MutableContext[JestSuite] {}

  val tests = Tests {
    val jsTestContainer = new JsTestContainer
    val suiteName = Random.genAlphanumeric(5)

    "normal-behavior" - {
      assert(jsTestContainer.getTests.isEmpty)
      val testName = Random.genAlphanumeric(20)
      val returnValue = Random.genAlphanumeric(40)
      jsTestContainer.add(testName, () => returnValue)
      assert(jsTestContainer.getTests.size == 1)
      val test = jsTestContainer.getTests.head
      assert(test.name == testName)
      val runBlock = test.runBlock.asInstanceOf[js.Function0[String]]
      assert(runBlock() == returnValue)

      jsTestContainer.setSuiteName(suiteName)
      assert(jsTestContainer.getSuiteName == suiteName)
      assert(jsTestContainer.getFilename == suiteName + ".test.js")
    }
    "constraint-behavior" - {
      intercept[IllegalStateException] {
        jsTestContainer.getSuiteName
      }
      intercept[IllegalStateException] {
        jsTestContainer.getFilename
      }
      intercept[IllegalArgumentException] {
        jsTestContainer.setSuiteName("bad name contains space")
      }
    }
    "negative-dupliciate-names" - {
      jsTestContainer.add("same", () => ())
      intercept[IllegalArgumentException](jsTestContainer.add("same", () => ()))
    }
    "order-retain" - {
      val expectedNames = (0 to Random.nextInt(50)).map { idx => Random.genAlphanumeric(2) + idx }
      expectedNames.foreach(jsTestContainer.add(_, () => ()))

      val resultNames = jsTestContainer.getTests.map(_.name)
      assert(resultNames == expectedNames)
    }
    "describe-group" - {
      assert(jsTestContainer.getGroups.size == 1)
      val groupCnt = Random.nextInt(6) + 1
      val expectedTestNames = (0 until groupCnt).flatMap { groupId =>
        val description = if (groupId == 0) None else Some(Random.genAlphanumeric(10))
        jsTestContainer.setDescribeGroup(description)
        val testNames = (0 to Random.nextInt(20)).map(_ => Random.genAlphanumeric(20))
        testNames.foreach(name => jsTestContainer.add(name, () => ()))

        testNames
      }

      val testGroups = jsTestContainer.getGroups
      assert(testGroups.size == groupCnt)

      val resultNames = testGroups.flatMap(_.getTests.map(_.name))
      assert(resultNames == expectedTestNames)
    }
  }

  override protected def propertyTestPathFilter: Seq[String] = Seq(
    "constraint-behavior", "negative-dupliciate-names"
  )
}
