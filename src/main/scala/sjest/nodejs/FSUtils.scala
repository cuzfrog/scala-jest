package sjest.nodejs

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}
import scala.scalajs.js.|

@js.native
@JSImport("fs", JSImport.Namespace)
private[sjest] object FileSystem extends js.Object {
  def openSync(file: String, flags: String | Int,
               mode: js.UndefOr[Int] = js.undefined): Int = js.native

  def closeSync(fd: Int): Unit = js.native

  def existsSync(file: String): Boolean = js.native

  def mkdirSync(file: String): Unit = js.native

  def writeFileSync(path: String | Int, data: String,
                    options: js.UndefOr[js.Object] = js.undefined): Unit = js.native

  def readFileSync(path: String,
                   options: js.UndefOr[js.Object | String] = js.undefined): js.Object = js.native

  def readdirSync(path: String,
                  options: js.UndefOr[js.Object | String] = js.undefined): Array[String] = js.native

  def unlinkSync(path: String): Unit = js.native

  def rmdirSync(path: String): Unit = js.native

  def lstatSync(path: String): FileStats = js.native
}

@js.native
@JSImport("path", JSImport.Namespace)
private[sjest] object Path extends js.Object {
  def dirname(file: String): String = js.native
  def resolve(path: String*): String = js.native
  def relative(from: String, to: String): String = js.native
}

@js.native
@JSName("Stats")
private[sjest] trait FileStats extends js.Object {
  def isFile(): Boolean = js.native
  def isDirectory(): Boolean = js.native
}

private[sjest] object FSUtils {

  import FileSystem._

  /** Write content to file, create dirs and file if they do not exist yet */
  def write(file: String, content: String): Unit = {
    ensureDirectoryExistence(file)
    writeFileSync(file, content)
  }

  /** Delete a given file or dir (recursively). Silence if given file does not exist */
  def delete(file: String): Unit = {
    if (existsSync(file)) {
      if (lstatSync(file).isDirectory()) {
        readdirSync(file).foreach { filename =>
          this.delete(Path.resolve(file, filename))
        }
        rmdirSync(file)
      } else {
        unlinkSync(file)
      }
    }
  }

  private def ensureDirectoryExistence(file: String) {
    var dirname = Path.dirname(file)
    if (!existsSync(dirname)) {
      ensureDirectoryExistence(dirname)
      mkdirSync(dirname)
    }
  }
}