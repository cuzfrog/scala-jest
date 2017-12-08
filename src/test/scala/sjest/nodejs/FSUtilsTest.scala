package sjest.nodejs

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path
import utest._

import scala.util.Random

object FSUtilsTest extends sjest.BaseSuite {

  private val randomPath = "/tmp/" + randomStr(5) + "/" + randomStr(5) + ".txt"
  private val expectedContent = Random.nextString(10000)

  override def utestBeforeEach(path: Seq[String]): Unit = cleanFile()

  override def utestAfterEach(path: Seq[String]): Unit = cleanFile()

  private def cleanFile(): Unit = {
    val dir = Path.dirname(randomPath)
    ChildProcess.execSync("rm -rf " + dir)
    assert(!Fs.existsSync(dir))
  }

  val tests = Tests {
    "write and delete" - {
      assert(!Fs.existsSync(randomPath))
      FSUtils.write(randomPath, expectedContent)
      assert(Fs.existsSync(randomPath))
      val content = Fs.readFileSync(randomPath).toString()
      assert(content == expectedContent)
      val dir = Path.dirname(randomPath)
      FSUtils.delete(dir)
      assert(!Fs.existsSync(randomPath))
      assert(!Fs.existsSync(dir))
    }
  }

  private def randomStr(n: Int): String = Random.alphanumeric.take(n).mkString
}
