package sjest.impl

import java.util.concurrent.atomic.AtomicReference

import com.softwaremill.macwire._
import sbt.testing.TaskDef
import sjest.support.VisibleForTest
import sjest.{GlobalStub, TestFrameworkConfig, impl}

/**
 * One module per application.
 * Note: master and slave are in two different applications, thus two Modules
 */
private[sjest] object Module {
  private val moduleRef: AtomicReference[ImplementationModule[_]] = new AtomicReference
  @VisibleForTest
  private[impl] def getModule: ImplementationModule[_] = {
    val module = moduleRef.get()
    if (module == null)
      throw new IllegalStateException("DI module not initiated.")
    module
  }

  def init[S](args: Array[String] = Array.empty,
              remoteArgs: Array[String] = Array.empty,
              globalStubOpt: Option[GlobalStub[S]] = None,
              communicationTunnel: Option[String => Unit] = None)
             (implicit config: TestFrameworkConfig): this.type = {
    val globalStub = globalStubOpt.getOrElse(GlobalStub.dummyGlobalStub)
    val newModule = new ImplementationModule(
      args.filter(_.startsWith(OPT_JS_PATH_KEY).unary_!), remoteArgs,
      globalStub, communicationTunnel)(updateOptJsPath(args, config))
    if (!moduleRef.compareAndSet(null, newModule))
      throw new IllegalStateException("DI module can only be initiated once.")
    this
  }

  def injectRunner: sbt.testing.Runner = getModule.jestRunner

  def defaultJestOutputFilter: String => String = JestOutputFilter.instance

  // ---- helpers ----
  @VisibleForTest
  private[impl] def updateOptJsPath(args: Array[String],
                                    config: TestFrameworkConfig): TestFrameworkConfig = {
    val pathOpt = args.find(_.startsWith(OPT_JS_PATH_KEY)).map { pathArg =>
      pathArg.drop(OPT_JS_PATH_KEY.length).trim
    }
    pathOpt match {
      case Some(p) =>
        if (config.optJsPath.nonEmpty) config
        else config.copy(optJsPath = p)
      case None => config
    }
  }
  @VisibleForTest
  private[impl] final val OPT_JS_PATH_KEY = "-opt.js.path:"
}

private class ImplementationModule[S](args: Array[String],
                                      remoteArgs: Array[String],
                                      globalSetupStub: GlobalStub[S],
                                      val communicationTunnel: Option[String => Unit])
                                     (implicit config: TestFrameworkConfig) {
  final type TaskFactory = TaskDef => JestTask
  final type CommunicationTunnel = String => Unit

  protected def jestRunnerArgs: JestRunner.Args = new JestRunner.Args(args)
  protected def jestRunnerRemoteArgs: JestRunner.RemoteArgs = new impl.JestRunner.RemoteArgs(args)
  final def isSlave: Boolean = communicationTunnel.isDefined

  // ------ Singletons ------
  @VisibleForTest lazy final val testStatistics: TestStatistics = wire[TestStatisticsImpl]
  protected lazy final val taskFactory: TaskFactory = (taskDef: TaskDef) => wire[JestTask]

  // ------ Prototypes ------
  protected final def jestOutputParser: JestOutputParser = wire[JestOutputParserImpl]
  protected final def jsTestGenerator: JsTestGenerator = wire[JsTestGeneratorImpl]
  @VisibleForTest final def nodejsTest: NodejsTest = wire[NodejsTestImpl]

  final def jestRunner: JestRunner = {
    if (isSlave) {
      val send = communicationTunnel.get
      wire[JestSlaveRunner[S]]
    } else wire[JestRunner]
  }
}