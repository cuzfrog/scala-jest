package sjest

import utest.TestSuite

import scala.concurrent.{ExecutionContext, Future}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

trait PropertyTest extends TestSuite {
  self: TestSuite =>

  protected def testRepeatTime: Int = 100

  //properties based test
  override def utestWrap(path: Seq[String],
                         runBody: => Future[Any])
                        (implicit ec: ExecutionContext): Future[Any] = {
    require(testRepeatTime > 0, "repetition must be at least 1 time.")
    val repetition: Seq[Future[Any]] = (1 to testRepeatTime).map(_ => super.utestWrap(path, runBody))
    Future.sequence(repetition).map { seq =>
      s"${seq.size} runs passed"
    }
  }
}
