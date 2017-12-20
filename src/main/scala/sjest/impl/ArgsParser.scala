package sjest.impl

import sjest.TestFrameworkConfig
import sjest.impl.ArgsParser._
import sjest.support.VisibleForTest

private final class ArgsParser private(args: Array[String]) {

  def update(config: TestFrameworkConfig): TestFrameworkConfig = {
    (updateOptJsPath _ andThen
      updateAutoRunTestInSbt andThen
      updateSilentOnPass).apply(config)
  }

  def filtered: Array[String] = {
    args.filter { arg =>
      !arg.startsWith(OPT_JS_PATH_KEY) &&
        arg != DISABLE_AUTO_RUN_TEST_IN_SBT &&
        arg != SILENT_ON_PASS
    }
  }

  @VisibleForTest
  private[impl] def updateOptJsPath(config: TestFrameworkConfig): TestFrameworkConfig = {
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
  private[impl] def updateAutoRunTestInSbt(config: TestFrameworkConfig): TestFrameworkConfig = {
    if (args.contains(DISABLE_AUTO_RUN_TEST_IN_SBT)) {
      config.copy(argsConfig = config.argsConfig.copy(autoRunTestInSbt = false))
    } else config
  }

  @VisibleForTest
  private[impl] def updateSilentOnPass(config: TestFrameworkConfig): TestFrameworkConfig = {
    if (args.contains(SILENT_ON_PASS)) {
      config.copy(argsConfig = config.argsConfig.copy(silentOnPass = true))
    } else config
  }


}

private object ArgsParser {
  def apply(args: Array[String]): ArgsParser = new ArgsParser(args)

  final val OPT_JS_PATH_KEY = "-opt.js.path:"
  final val DISABLE_AUTO_RUN_TEST_IN_SBT = "-disableAutoRunTestInSbt"
  final val SILENT_ON_PASS = "-silentOnPass"
}