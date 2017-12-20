package sjest.impl

import sjest.{BaseSuite, PropertyTest}
import sjest.MockObjects.mockConfig
import utest._

import scala.util.Random


object ArgsParserTest extends BaseSuite with PropertyTest {
  val tests = Tests {
    "updateOptJsPath" - {
      val pathInArg = Random.genAlphanumeric(10) + "some.path.to.opt.js"
      val args: Array[String] = Array(
        ArgsParser.OPT_JS_PATH_KEY + pathInArg,
        "noise" + Random.genAlphanumeric(5)
      )
      val impl = ArgsParser(args)

      "config prior to args" - {
        val config = impl.updateOptJsPath(mockConfig)
        assert(config.optJsPath == mockConfig.optJsPath)
      }
      "args" - {
        val config = impl.updateOptJsPath(mockConfig.copy(optJsPath = ""))
        assert(config.optJsPath == pathInArg)
      }
      "filtered" - {
        assert(!impl.filtered.contains(ArgsParser.OPT_JS_PATH_KEY))
      }
    }
    "updateAutoRunTestInSbt" - {
      "disabled" - {
        val args = Array(ArgsParser.DISABLE_AUTO_RUN_TEST_IN_SBT)
        val impl = ArgsParser(args)
        val config = impl.updateAutoRunTestInSbt(mockConfig)
        assert(!config.argsConfig.autoRunTestInSbt)
        assert(!impl.filtered.contains(ArgsParser.DISABLE_AUTO_RUN_TEST_IN_SBT))
      }
      "enabled" - {
        val impl = ArgsParser(Array.empty)
        val config = impl.updateAutoRunTestInSbt(mockConfig)
        assert(config.argsConfig.autoRunTestInSbt)
      }
    }
    "updateSilentOnPass" - {
      "specified" - {
        val args = Array(ArgsParser.SILENT_ON_PASS)
        val impl = ArgsParser(args)
        val config = impl.updateSilentOnPass(mockConfig)
        assert(config.argsConfig.silentOnPass)
        assert(!impl.filtered.contains(ArgsParser.SILENT_ON_PASS))
      }
      "not specified" - {
        val impl = ArgsParser(Array.empty)
        val config = impl.updateSilentOnPass(mockConfig)
        assert(!config.argsConfig.silentOnPass)
      }
    }
  }
}
