package sjest

import sbt.testing.{SuiteSelector, TaskDef}
import sjest.impl.JsTestStubTest

import scala.scalajs.js

private object MockObjects {
  implicit val mockConfig: TestFrameworkConfig = JestFramework.defaultConfig.copy(
    optJsPath = "target/tmp/dir1/fastopt.js",
    testJsDir = "target/tmp/jests/",
    nodejsCmdOfPath = _ => JestFramework.NodejsCmd("/usr/bin/npm", js.Array("test")),
    autoRunTestInSbt = true
  )

  def newTaskDef(fqcn: String): TaskDef =
    new TaskDef(
      _fullyQualifiedName = fqcn,
      _fingerprint = new JestFingerprint,
      _explicitlySpecified = false,
      _selectors = Array(new SuiteSelector)
    )
}

private final class MockSuccessTest extends JestSuite {
  test("mock test") {
    JsTestStubTest.sideEffectMarker += 1
  }
}