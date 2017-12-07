package sjest.impl

import java.util.concurrent.atomic.AtomicReference

import com.softwaremill.macwire._
import sbt.testing.TaskDef
import sjest.support.VisibleForTest
import sjest.{TestFrameworkConfig, impl}

/**
 * One module per application.
 * Note: master and slave are in two different applications, thus two Modules
 */
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
           remoteArgs: Array[String] = Array.empty,
           communicationTunnel: Option[String => Unit] = None)
          (implicit config: TestFrameworkConfig): this.type = {
    val newModule = new ImplementationModule(args, remoteArgs, communicationTunnel)
    if (!moduleRef.compareAndSet(null, newModule))
      throw new IllegalStateException("DI module can only be initiated once.")
    this
  }

  def injectRunner: sbt.testing.Runner = getModule.jestRunner

  val defaultJestOutputFilter: String => String = JestOutputFilter.instance
}

private class ImplementationModule(args: Array[String],
                                   remoteArgs: Array[String],
                                   communicationTunnel: Option[String => Unit])
                                  (implicit config: TestFrameworkConfig) {
  final type TaskFactory = TaskDef => JestTask
  final type CommunicationTunnel = String => Unit

  private def jestRunnerArgs: JestRunner.Args = new JestRunner.Args(args)
  private def jestRunnerRemoteArgs: JestRunner.RemoteArgs = new impl.JestRunner.RemoteArgs(args)
  final def isSlave: Boolean = communicationTunnel.isDefined

  // ------ Singletons ------
  @VisibleForTest lazy final val testStatistics: TestStatistics = wire[TestStatisticsImpl]
  protected lazy final val taskFactory: TaskFactory = (taskDef: TaskDef) => wire[JestTask]

  // ------ Prototypes ------
  protected final def jestOutputParser: JestOutputParser = wire[JestOutputParserImpl]
  protected final def jsTestConverter: JsTestGenerator = wire[JsTestGeneratorImpl]
  @VisibleForTest final def nodejsTest: NodejsTest = wire[NodejsTestImpl]

  final def jestRunner: JestRunner = {
    if (isSlave) {
      val send = communicationTunnel.get
      wire[JestSlaveRunner]
    } else wire[JestRunner]
  }
}