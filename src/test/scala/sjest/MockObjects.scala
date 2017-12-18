package sjest

import sbt.testing.{SuiteSelector, TaskDef}
import sjest.conversion.JsTestStubTest

private object MockObjects {
  implicit val mockConfig: TestFrameworkConfig = JestFramework.defaultConfig.copy(
    optJsPath = "target/tmp/dir1/fastopt.js",
    testJsDir = "target/tmp/jests/"
  )

  def newTaskDef(fqcn: String = mockSuccessTestFqcn): TaskDef =
    new TaskDef(
      _fullyQualifiedName = fqcn,
      _fingerprint = new JestFingerprint,
      _explicitlySpecified = false,
      _selectors = Array(new SuiteSelector)
    )

  val mockSuccessTestFqcn: String = MockSuccessTest.suiteName
}

private object MockSuccessTest extends JestSuite {
  import MutableContexts.jestSuiteContext

  test("mock test") {
    JsTestStubTest.sideEffectMarker += 1
  }


  val suiteName: String =
    if(getClass.getName.endsWith("$")) getClass.getName.dropRight(1) else getClass.getName
  this.container.setSuiteName(suiteName)
}