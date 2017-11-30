package sjest

import sbt.testing.{Fingerprint, Runner}
import sjest.JestFramework.NodejsCmd

abstract class JestFramework extends sbt.testing.Framework {
  override final def name(): String = "simple-jest"

  override final def fingerprints(): Array[Fingerprint] = Array(new JestFingerprint)

  override final def runner(args: Array[String],
                            remoteArgs: Array[String],
                            testClassLoader: ClassLoader): Runner = {
    new JestRunner(args, remoteArgs, testClassLoader)
  }

  override final def slaveRunner(args: Array[String],
                                 remoteArgs: Array[String],
                                 testClassLoader: ClassLoader,
                                 send: String => Unit): Runner = {
    new JestRunner(args, remoteArgs, testClassLoader)
  }

  private implicit final def assembleConfig: TestFrameworkConfig = {
    TestFrameworkConfig(
      this.optJsPath,
      this.testJsDir,
      this.nodejsCmd,
      this.autoRunTestInSbt)
  }

  //client api:
  /** *opt.js full path or path relative to sbt root dir. */
  protected def optJsPath: String
  /** Generated *.test.js full path or path relative to sbt root dir. */
  protected def testJsDir: String = "./target/simple-jests/"
  /** Yield npm test command, given a test.js file path.<br>
   * /dev/null is used to suppress jest output in sbt console.
   */
  protected def nodejsCmd(jsTestPath: String): NodejsCmd =
    NodejsCmd("npm", Array("test", "--", jsTestPath, "--colors"))
  /** Whether to run npm test in sbt. <br>
   * 'npm test -- xx.test.js' is executed for every test, thus worse performance.
   * One could disable it by set this to false, and manually run jest from command line.
   */
  protected def autoRunTestInSbt: Boolean = true
}

object JestFramework{
  final case class NodejsCmd(cmd: String, args: Array[String])
}