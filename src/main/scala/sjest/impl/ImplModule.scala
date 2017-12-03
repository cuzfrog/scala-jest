package sjest.impl

import com.softwaremill.macwire._

private sealed trait ImplModule {
  lazy val jestOutputFilter: JestOutputFilter = wire[JestOutputFilterDefaultImpl]
  lazy val nodejsTest: NodejsTest = wire[NodejsTestImpl]
}

private object ImplModule extends ImplModule

private object ImplModuleForTest extends ImplModule {
  lazy val nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
}