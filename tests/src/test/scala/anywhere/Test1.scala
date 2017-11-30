package anywhere

object Test1 extends sjest.JestSuite {
  test("this is my first test") {
    println("do some test1!")
    expect(1 + 2).toBe(3)
  }

  test("this is a failed test") {
    expect(1 + 1).toBe(5)
  }


  //
  //  test("dom test"){
  //    val div = org.scalajs.dom.document.createElement("div")
  //    val body = org.scalajs.dom.document.body.appendChild(div)
  //
  //  }
}
