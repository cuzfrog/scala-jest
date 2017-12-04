package sjest.impl

import com.softwaremill.macwire._
import sjest.{JestFramework, TestFrameworkConfig}

private final case class ModuleForTest(args: Array[String] = Array.empty,
                                       remoteArgs: Array[String] = Array.empty,
                                       testClassLoader: ClassLoader = null)
                                      (implicit config: TestFrameworkConfig = JestFramework.defaultConfig)
  extends ImplementationModule(args, remoteArgs, testClassLoader) {
  // ------ for test ------
  def nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
  //  override def jsTestConverter: JsTestConverter = super.jsTestConverter
  //  override def jestOutputParser: JestOutputParser = super.jestOutputParser
  //  override val testStatistics: TestStatistics = this.testStatistics
}
