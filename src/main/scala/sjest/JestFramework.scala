package sjest

import sbt.testing.{Fingerprint, Runner}

class JestFramework extends sbt.testing.Framework {
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
    this.runner(args, remoteArgs, testClassLoader)
  }
}
