package sjest

import utest.TestSuite
import utest.framework.Tree

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

trait PropertyTest extends sjest.BaseSuite {
  self: TestSuite =>

  protected def propertyTestRepeatTime: Int = 100
  protected def propertyTestPathExcluded: Seq[String] = Seq.empty

  //properties based test
  override final def utestWrap(path: Seq[String],
                         runBody: => Future[Any])
                        (implicit ec: ExecutionContext): Future[Any] = {
    if (path.intersect(propertyTestPathExcluded).nonEmpty) runBody
    else {
      require(propertyTestRepeatTime > 0, "repetition must be at least 1 time.")
      val repetition: Seq[Future[Any]] =
        (1 to propertyTestRepeatTime).map(_ => super.utestWrap(path, runBody))
      Future.sequence(repetition).map { seq =>
        s"${seq.size} runs passed"
      }
    }
  }

  override final def utestAfterAll(): Unit = {
    checkPathsInPathFilter()
    super.utestAfterAll()
  }

  /** Check if path filters are not matched with test paths. */
  private def checkPathsInPathFilter(): Unit = {
    val paths = extractPaths(this.tests.nameTree)
    this.propertyTestPathExcluded.foreach { p =>
      if (!paths.contains(p))
        throw new IllegalArgumentException(s"Cannot find filter '$p' in test path.")
    }
  }

  private def extractPaths(nameTree: Tree[String],
                           paths: Seq[String] = Seq.empty): Seq[String] = {
    if (nameTree.children.isEmpty) paths :+ nameTree.value
    else nameTree.children.flatMap(extractPaths(_, paths))
  }
}
