package anywhere

import sjest.JestSuite

object Test2 extends JestSuite {

  var marker: Int = 0

  beforeAll {
    marker += 1
  }

  beforeEach {
    marker += 1
  }

  afterEach {
    marker += 1
  }

  afterAll {
    println(marker) //9
  }

  describe("outer1") {
    describe("inner2") {
      test("test2-1") {
        marker += 1
        println("do some test2-1!")
      }
    }
    test("test2-3") {
      println("do some test2-3!")
    }
  }

  test("test2-2") {
    marker += 1
    println("do some test2-2!")
  }
}
