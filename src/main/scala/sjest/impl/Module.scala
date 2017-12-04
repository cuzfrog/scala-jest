package sjest.impl

import java.util.concurrent.atomic.AtomicReference

import com.softwaremill.macwire._
import sbt.testing.TaskDef
import sjest.{TestFrameworkConfig, impl}

private[sjest] object Module {
  private val moduleRef: AtomicReference[ImplementationModule] = new AtomicReference
  private def getModule: ImplementationModule = {
    val module = moduleRef.get()
    if (module == null)
      throw new IllegalStateException("DI module not initiated.")
    module
  }

  def init(args: Array[String],
           remoteArgs: Array[String],
           testClassLoader: ClassLoader)
          (implicit config: TestFrameworkConfig): this.type = {
    if (moduleRef.get() != null)
      throw new IllegalStateException("DI module can only be initiated once.")
    moduleRef.set(new ImplementationModule(args, remoteArgs, testClassLoader))
    this
  }

  def injectRunner: sbt.testing.Runner = getModule.injectRunner

  val defaultJestOutputFilter: String => String = JestOutputFilter.instance
}

private class ImplementationModule(args: Array[String],
                                   remoteArgs: Array[String],
                                   testClassLoader: ClassLoader)
                                  (implicit config: TestFrameworkConfig) {
  type TaskFactory = TaskDef => sbt.testing.Task

  private def jestRunnerArgs: JestRunner.Args = new JestRunner.Args(args)
  private def jestRunnerRemoteArgs: JestRunner.RemoteArgs = new impl.JestRunner.RemoteArgs(args)

  // ------ Singletons ------
  protected lazy val testStatistics: TestStatistics = wire[TestStatisticsImpl]
  protected lazy val taskFactory: TaskFactory = (taskDef: TaskDef) => wire[JestTask]

  // ------ Prototypes ------
  protected def jestOutputParser: JestOutputParser = wire[JestOutputParserImpl]
  protected def jsTestConverter: JsTestConverter = wire[JsTestConverterImpl]
  protected def nodejsTest: NodejsTest = wire[NodejsTestImpl]
  def injectRunner: sbt.testing.Runner = wire[JestRunner]
}