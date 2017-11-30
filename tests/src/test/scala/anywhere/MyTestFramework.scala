package anywhere

import sjest.JestFramework

private final class MyTestFramework extends JestFramework {
  private val project = "tests"
  override protected def optJsPath = s"$project/target/scala-2.12/simple-jest-tests-test-fastopt.js"
  override protected def testJsDir = s"$project/target/simple-jests/"
}
