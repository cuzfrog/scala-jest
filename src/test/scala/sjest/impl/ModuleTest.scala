package sjest.impl

import sjest.PropertyTest
import utest._

import scala.util.Random

/**
 * To test scope and relationship between dependencies.
 */
object ModuleTest extends sjest.BaseSuite with PropertyTest {

  import sjest.MockObjects.mockConfig

  val tests = Tests {
    "wrapper-state" - {
      intercept[IllegalStateException](Module.getModule)
      intercept[IllegalStateException](Module.init().init())
    }
    //above must be at top to do the init
    lazy val module = Module.getModule
    "testStatistics-integration" - {
      val nodejsTest = module.nodejsTest.asInstanceOf[NodejsTestImpl]
      val testStatistics = module.testStatistics
      assert(testStatistics eq nodejsTest.testStatistics)
    }
    val pathInArg = Random.genAlphanumeric(10) + "some.path.to.opt.js"
    val args = Array(Module.OPT_JS_PATH_KEY + pathInArg, "noise" + Random.genAlphanumeric(5))
    "updateOptJsPath-config-prior" - {
      val config = Module.updateOptJsPath(args, mockConfig)
      assert(config.optJsPath == mockConfig.optJsPath)
    }
    "updateOptJsPath" - {
      val config = Module.updateOptJsPath(args, mockConfig.copy(optJsPath = ""))
      assert(config.optJsPath == pathInArg)
    }
  }

  override protected def propertyTestPathExcluded = Seq(
    "wrapper-state", "testStatistics-integration"
  )
}
