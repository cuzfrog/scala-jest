package sjest

import utest.TestSuite

import scala.concurrent.{ExecutionContext, Future}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

trait PropertyTest extends TestSuite {
  self: TestSuite =>

  protected def propertyTestRepeatTime: Int = 100
  protected def propertyTestPathFilter: Seq[String] = Seq.empty

  //properties based test
  override def utestWrap(path: Seq[String],
                         runBody: => Future[Any])
                        (implicit ec: ExecutionContext): Future[Any] = {
    if (path.intersect(propertyTestPathFilter).nonEmpty) runBody
    else {
      require(propertyTestRepeatTime > 0, "repetition must be at least 1 time.")
      val repetition: Seq[Future[Any]] = (1 to propertyTestRepeatTime).map(_ => super.utestWrap(path, runBody))
      Future.sequence(repetition).map { seq =>
        s"${seq.size} runs passed"
      }
    }
  }


}
