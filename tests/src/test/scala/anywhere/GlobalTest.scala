package anywhere

import scala.collection.mutable
import scala.util.Random

object GlobalTest1 extends sjest.JestSuite {
  test("modify global marker1") {
    GlobalMarker.mark(1)
  }

  test("modify global marker2") {
    GlobalMarker.mark(1)
  }

  //afterAll(GlobalMarker.show(this.getClass.getName))
}

object GlobalTest2 extends sjest.JestSuite {
  test("modify global marker1") {
    GlobalMarker.mark(1)
  }

  test("modify global marker2") {
    GlobalMarker.mark(1)
  }

  //afterAll(GlobalMarker.show(this.getClass.getName))
}

object GlobalMarker {
  private var value: Int = 0 //limited scope in single test suite

  def mark(n: Int): Unit = this.value += n
  def show(name: String): Unit = println(s"Global marker value: $value in: $name")
}

private class GlobalMarker {
  val signature: Int = Random.nextInt(9999999)
  //println(s"GlobalMarker created, sig-$signature")
  var assertMarker: Boolean = false
  var values: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty

  def mark(v: Int): Unit = {
    if (v > 0) assertMarker = true
    values += v
  }
}