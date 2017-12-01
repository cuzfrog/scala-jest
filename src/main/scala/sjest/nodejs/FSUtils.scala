package sjest.nodejs

import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.path.Path

private[sjest] object FSUtils {

  /** Write content to file, create dirs and file if they do not exist yet
   *
   * @return file path */
  def write(file: String, content: String): String = {
    ensureDirectoryExistence(file)
    Fs.writeFileSync(file, content)
    file
  }

  /** Delete a given file or dir (recursively). Silence if given file does not exist */
  def delete(file: String): Unit = {
    if (Fs.existsSync(file)) {
      if (Fs.lstatSync(file).isDirectory()) {
        Fs.readdirSync(file).foreach { filename =>
          this.delete(Path.resolve(file, filename.toString))
        }
        Fs.rmdirSync(file)
      } else {
        Fs.unlinkSync(file)
      }
    }
  }

  private def ensureDirectoryExistence(file: String) {
    var dirname = Path.dirname(file)
    if (!Fs.existsSync(dirname)) {
      ensureDirectoryExistence(dirname)
      Fs.mkdirSync(dirname)
    }
  }
}