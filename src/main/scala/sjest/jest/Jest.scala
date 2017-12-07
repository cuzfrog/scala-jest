package sjest.jest

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
sealed trait Jest extends js.Object {
  def clearAllTimers(): Unit = js.native
  def clearAllMocks(): Jest = js.native
  def resetAllMocks(): Jest = js.native
  def useFakeTimers(): Jest = js.native
  def useRealTimers(): Jest = js.native
  def runAllTicks(): Jest = js.native
  def runTimersToTime(msToRun: Int): Jest = js.native
  def setTimeout(timeout: Int): Jest = js.native
  def runAllImmediates(): Jest = js.native
  def runAllTimers(): Jest = js.native
  def runOnlyPendingTimers(): Jest = js.native
  def resetModules(): Jest = js.native
  def disableAutomock(): Jest = js.native
  def enableAutomock(): Jest = js.native
  def fn(implementation: js.Function): js.Function = js.native
  def isMockFunction(fn: js.Function): Boolean = js.native
  def genMockFromModule(moduleName: String): js.Function = js.native
  def mock(moduleName: String,
           factory: js.Function = ???,
           options: js.Object = ???): Jest = js.native
  def doMock(moduleName: String,
             factory: js.Function = ???,
             options: js.Object = ???): Jest = js.native
  def unmock(moduleName: String): Jest = js.native
  def dontMock(moduleName: String): Jest = js.native
  def setMock(moduleName: String, moduleExports: js.Any): Jest =
    js.native
  def spyOn(obj: js.Object, methodName: String): Jest = js.native
}

@JSGlobal("jest")
@js.native
object Jest extends Jest
