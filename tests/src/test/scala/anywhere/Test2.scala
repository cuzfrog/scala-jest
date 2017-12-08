package anywhere

import sjest.JestSuite

final class Test2 extends JestSuite {

  beforeAll{
    println("--before all--")
  }

  beforeEach{
    println("--before each--")
  }

  afterEach{
    println("--after each--")
  }

  afterAll{
    println("--after all--")
  }

  describe("outer1"){
    describe("inner2"){
      test("test2-1") {
        println("do some test2-1!")
      }
    }
  }

  test("test2-2") {
    println("do some test2-2!")
  }

}
