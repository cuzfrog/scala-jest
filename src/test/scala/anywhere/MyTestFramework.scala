package anywhere

import sjest.JestFramework

private final class MyTestFramework extends JestFramework {
  override protected def optJsPath = "target/scala-2.12/simple-jest-test-fastopt.js"

}
