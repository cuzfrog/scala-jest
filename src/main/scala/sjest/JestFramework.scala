package sjest

import io.scalajs.nodejs.fs.Fs
import sbt.testing.{Fingerprint, Runner}
import sjest.JestFramework.NodejsCmd
import sjest.impl.Module

import scala.scalajs.js

class JestFramework extends sbt.testing.Framework {
  override final def name(): String = "scala-jest"

  override final def fingerprints(): Array[Fingerprint] = Array(new JestFingerprint)

  override final def runner(args: Array[String],
                            remoteArgs: Array[String],
                            testClassLoader: ClassLoader): Runner = {
    Module.init(args, remoteArgs, assembleGlobalStub).injectRunner
  }

  override final def slaveRunner(args: Array[String],
                                 remoteArgs: Array[String],
                                 testClassLoader: ClassLoader,
                                 send: String => Unit): Runner = {
    Module.init(args, remoteArgs, assembleGlobalStub, Some(send)).injectRunner
    //note: master and slave are in two different applications, thus two Modules
  }

  import JestFramework.defaultConfig

  private implicit final def assembleConfig: TestFrameworkConfig = {
    TestFrameworkConfig(
      this.optJsPath,
      this.testJsDir,
      this.nodejsCmd,
      this.sourceMapSupport,
      this.jestOutputFilter,
      defaultConfig.argsConfig
    )
  }
  private final def assembleGlobalStub = Some(GlobalStub(beforeGlobal(), afterGlobal))

  //------------------ client custom api -------------------
  /** *opt.js full path or path relative to sbt root dir. This is prior to args */
  protected def optJsPath: String = defaultConfig.optJsPath

  /** Generated *.test.js full path or path relative to sbt root dir. */
  protected def testJsDir: String = defaultConfig.testJsDir

  /** Yield test command, given a test.js file path.  Jest configs can be put here. */
  protected def nodejsCmd(jsTestPath: String): NodejsCmd = defaultConfig.nodejsCmdOfPath(jsTestPath)

  /** Source map support in stack trace. */
  protected def sourceMapSupport: String = defaultConfig.sourceMapSupport

  /** Filter jest output in sbt console */
  protected def jestOutputFilter: String => String = defaultConfig.jestOutputFilter

  /** Setup before run */
  protected def beforeGlobal(): Any = ()
  /** Teardown after run. The stub is returned from `beforeGlobal()` */
  protected def afterGlobal(stub: Any): Any = stub
}

object JestFramework {
  final case class NodejsCmd(cmd: String, args: js.Array[String]) {
    require(Fs.existsSync(cmd), s"Cannot find '$cmd', please use path.")
  }

  private[sjest] lazy val defaultConfig = TestFrameworkConfig(
    optJsPath = "",
    testJsDir = "./target/scala-jests/",
    nodejsCmdOfPath = (jsTestPath: String) =>
      NodejsCmd("node_modules/jest-cli/bin/jest.js", js.Array("--colors", jsTestPath)),
    sourceMapSupport = "require('source-map-support').install();",
    jestOutputFilter = Module.defaultJestOutputFilter,
    argsConfig = ArgsConfig(
      autoRunTestInSbt = true,
      silentOnPass = false
    )
  )
}

private final case class TestFrameworkConfig(optJsPath: String,
                                             testJsDir: String,
                                             nodejsCmdOfPath: String => JestFramework.NodejsCmd,
                                             sourceMapSupport: String,
                                             jestOutputFilter: String => String,
                                             argsConfig: ArgsConfig)

private case class ArgsConfig(autoRunTestInSbt: Boolean,
                              silentOnPass: Boolean)

private final case class GlobalStub[S](setupResult: S, globalTeardown: S => Unit)
private object GlobalStub {
  val dummyGlobalStub: GlobalStub[Unit] = GlobalStub[Unit]((), _ => ())
}