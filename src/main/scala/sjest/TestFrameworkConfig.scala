package sjest

private case class TestFrameworkConfig(optJsPath: String,
                                       testJsDir: String,
                                       npmCmdOfPath: String => JestFramework.NodejsCmd,
                                       autoRunTestInSbt: Boolean)
