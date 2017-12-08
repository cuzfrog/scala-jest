package sjest.impl

import utest._

/**
 * To test scope and relationship between dependencies.
 */
object ModuleTest extends sjest.BaseSuite {

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
  }
}
