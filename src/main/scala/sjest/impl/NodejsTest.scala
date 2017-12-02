package sjest.impl

import sbt.testing.{Event, Logger, TaskDef}
import sjest.TestFrameworkConfig

private[sjest] trait NodejsTest {
  def runTest(jsTestPath: String, loggers: Array[Logger])
             (implicit taskDef: TaskDef, config: TestFrameworkConfig): Event
}

private[sjest] object NodejsTest {
  def apply(): NodejsTest = new NodejsTestImpl()
}
