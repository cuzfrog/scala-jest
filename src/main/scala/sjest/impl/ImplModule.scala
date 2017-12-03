package sjest.impl

import com.softwaremill.macwire._

private[sjest] sealed trait ImplModule {
  lazy final val jestOutputFilter: JestOutputFilter = wire[JestOutputFilterDefaultImpl]
  lazy final val nodejsTest: NodejsTest = wire[NodejsTestImpl]
  lazy final val testStatistics: TestStatistics = wire[TestStatisticsImpl]
}

private[sjest] object ImplModule extends ImplModule

private object ImplModuleForTest extends ImplModule {
  lazy final val nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
}