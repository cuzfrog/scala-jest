package anywhere

import scala.scalajs.js

object TmpApp {
  def main(args: Array[String]): Unit = {
    println("asdf")
  }
}

class MyJsObject extends js.Object {
  private var value = 0

  def act(): Unit = {
    value += 1
    println(value)
  }
}