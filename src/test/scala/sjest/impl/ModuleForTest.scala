package sjest.impl

import com.softwaremill.macwire._
import sjest.{JestFramework, MockObjects, TestFrameworkConfig}

private final
case class ModuleForTest(args: Array[String] = Array.empty,
                         remoteArgs: Array[String] = Array.empty)
                        (implicit config: TestFrameworkConfig = JestFramework.defaultConfig)
  extends ImplementationModule(args, remoteArgs)
    with Product with Serializable { module =>

  object Singleton {
    def testStatistics: TestStatistics = module.testStatistics
  }

  object Prototype {
    def nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
    def jsTestConverter: JsTestConverter = module.jsTestConverter
    def jestOutputParser: JestOutputParser = module.jestOutputParser
    def testStatistics: TestStatistics = wire[TestStatisticsImpl]
    def jestTask(fqcn: String): JestTask = module.taskFactory(MockObjects.newTaskDef(fqcn))
  }
}