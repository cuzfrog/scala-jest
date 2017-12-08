package anywhere

import sjest.JestFramework

import scala.collection.mutable
import scala.util.Random

private final class MyTestFramework extends JestFramework {
  private val project = "tests"
  override protected def optJsPath = s"$project/target/scala-2.12/sjest-tests-test-fastopt.js"
  override protected def testJsDir = s"$project/target/sjests/"

  override protected def beforeGlobal(): Any = {
    val globalMarker = new GlobalMarker
    globalMarker.mark(0)
    globalMarker
  }
  override protected def afterGlobal(setupStub: Any): Any = {
    val globalMarker = setupStub.asInstanceOf[GlobalMarker]
    if (globalMarker.assertMarker) {
      val expectedValues = Seq(0, 1, 2)
      assert(globalMarker.values == expectedValues)
    }
    println(s"global marker after global, sig-${globalMarker.signature}, values:${globalMarker.values}")
  }
}

private class GlobalMarker {
  val signature: Int = Random.nextInt(9999999)
  println(s"GlobalMarker created, sig-$signature")
  var assertMarker: Boolean = false
  var values: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty

  def mark(v: Int): Unit = {
    if (v > 0) assertMarker = true
    values += v
  }
}