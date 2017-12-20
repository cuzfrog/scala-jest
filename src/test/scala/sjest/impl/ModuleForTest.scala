package sjest.impl

import com.softwaremill.macwire._
import sjest.{GlobalStub, JestFramework, MockObjects, TestFrameworkConfig}

private final
case class ModuleForTest(args: Array[String] = Array.empty)
                        (implicit config: TestFrameworkConfig = JestFramework.defaultConfig)
  extends ImplementationModule(args, GlobalStub.dummyGlobalStub, None)
    with Product with Serializable { module =>

  object Singleton {
    //lazy val jsTestConverter: JsTestConverter = module.jsTestConverter
    lazy val jestRunnerArgs: JestRunner.Args = module.jestRunnerArgs
    lazy val jestOutputParser: JestOutputParser = module.jestOutputParser
    lazy val testStatistics: TestStatistics = module.testStatistics
    lazy val nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
  }

  object Prototype {
    def jestRunnerArgs: JestRunner.Args = module.jestRunnerArgs
    def nodejsTestImpl: NodejsTestImpl = wire[NodejsTestImpl]
    def jsTestGenerator: JsTestGenerator = module.jsTestGenerator
    def jestOutputParser: JestOutputParser = module.jestOutputParser
    def testStatistics: TestStatistics = wire[TestStatisticsImpl]
    def jestTask(fqcn: String): JestTask = module.taskFactory(MockObjects.newTaskDef(fqcn))
  }
}