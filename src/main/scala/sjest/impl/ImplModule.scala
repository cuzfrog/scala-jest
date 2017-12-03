package sjest.impl

import com.softwaremill.macwire._

private[sjest] sealed trait ImplModule {
  lazy final val jestOutputFilter: JestOutputFilter = wire[JestOutputFilterDefaultImpl]
  lazy final val nodejsTest: NodejsTest = wire[NodejsTestImpl]
  private[impl] lazy final val jestOutputParser: JestOutputParser = wire[JestOutputParserImpl]

  final def testStatistics: TestStatistics = wire[TestStatisticsImpl]
}

private[sjest] object ImplModule extends ImplModule

private object ImplModuleForTest extends ImplModule {
  def _jestOutputParser: JestOutputParser = this.jestOutputParser
  lazy final val nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
}