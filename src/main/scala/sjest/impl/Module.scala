package sjest.impl

import java.util.concurrent.atomic.AtomicReference

import com.softwaremill.macwire._
import sbt.testing.TaskDef
import sjest.support.VisibleForTest
import sjest.{TestFrameworkConfig, impl}

private[sjest] object Module {
  private val moduleRef: AtomicReference[ImplementationModule] = new AtomicReference
  @VisibleForTest
  private[impl] def getModule: ImplementationModule = {
    val module = moduleRef.get()
    if (module == null)
      throw new IllegalStateException("DI module not initiated.")
    module
  }

  def init(args: Array[String] = Array.empty,
           remoteArgs: Array[String] = Array.empty)
          (implicit config: TestFrameworkConfig): this.type = {
    if (moduleRef.get() != null)
      throw new IllegalStateException("DI module can only be initiated once.")
    moduleRef.set(new ImplementationModule(args, remoteArgs))
    this
  }

  def injectRunner: sbt.testing.Runner = getModule.injectRunner

  val defaultJestOutputFilter: String => String = JestOutputFilter.instance
}

private class ImplementationModule(args: Array[String],
                                   remoteArgs: Array[String])
                                  (implicit config: TestFrameworkConfig) {
  type TaskFactory = TaskDef => JestTask

  private def jestRunnerArgs: JestRunner.Args = new JestRunner.Args(args)
  private def jestRunnerRemoteArgs: JestRunner.RemoteArgs = new impl.JestRunner.RemoteArgs(args)

  // ------ Singletons ------
  @VisibleForTest lazy val testStatistics: TestStatistics = wire[TestStatisticsImpl]
  protected lazy val taskFactory: TaskFactory = (taskDef: TaskDef) => wire[JestTask]

  // ------ Prototypes ------
  protected def jestOutputParser: JestOutputParser = wire[JestOutputParserImpl]
  protected def jsTestConverter: JsTestConverter = wire[JsTestConverterImpl]
  @VisibleForTest def nodejsTest: NodejsTest = wire[NodejsTestImpl]

  def injectRunner: sbt.testing.Runner = wire[JestRunner]
}