package sjest

import sbt.testing.{SuiteSelector, TaskDef}
import sjest.impl.JsTestStubTest

import scala.scalajs.js

private object MockObjects {
  implicit val mockConfig: TestFrameworkConfig = JestFramework.defaultConfig.copy(
    optJsPath = "target/tmp/dir1/fastopt.js",
    testJsDir = "target/tmp/jests/"
  )

  def newTaskDef(fqcn: String = "sjest.MockSuccessTest"): TaskDef =
    new TaskDef(
      _fullyQualifiedName = fqcn,
      _fingerprint = new JestFingerprint,
      _explicitlySpecified = false,
      _selectors = Array(new SuiteSelector)
    )

  val mockSuccessTestFqcn: String = classOf[MockSuccessTest].getName
}

private final class MockSuccessTest extends JestSuite {
  test("mock test") {
    JsTestStubTest.sideEffectMarker += 1
  }
}