package sjest

private case class TestFrameworkConfig(optJsPath: String,
                                       testJsDir: String,
                                       nodejsCmdOfPath: String => JestFramework.NodejsCmd,
                                       autoRunTestInSbt: Boolean)