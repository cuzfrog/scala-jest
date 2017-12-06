# Scala-jest
Write `jest` tests as scala classes and run them within sbt without any hassle.

### Motivation
* To workaround scala.js issue [#2635](https://github.com/scala-js/scala-js/issues/2635).
* To provide a better test ability against nodejs + jsdom + CommonJS on scala.js.
* That is: test your scala.js-react project just by `sbt test`

![scala-jest-demo.png](demo-pic/scala-jest-demo.png)

### How it works?
Scala-jest exposes client tests in `*opt.js`, and generates `*.test.js` files to call them,
executed by nodejs' `child_process`, which then triggers [jest](https://facebook.github.io/jest).
So scala.js is not involved the when testing is running, only the stdout/err shown in sbt console.

A drawback is that file paths are not managed by sbt, and must be set manually.

### Setup

Prerequisite:

* nodejs (probably higher than v8.7.0) 
* a `package.json`, or `npm init` to make one
* `npm install --save-dev jest`

_See scala.js doc for file path conventions._

Dependency:

    libraryDependencies += "com.github.cuzfrog" %%% "sjest" % "0.1.0-SNAPSHOT" Test

Provide test Framework(to specify client build info):

```scala
import sjest.JestFramework
private final class MyTestFramework extends JestFramework {
  private val project = "tests"
  //where the *opt.js file is:
  override protected def optJsPath = s"$project/target/scala-2.12/sjest-tests-test-fastopt.js"
  //where to put generated *.test.js files:
  override protected def testJsDir = s"$project/target/sjests/"
}
```  

### It's simple to use:

Write your `jest` test in a class:

```scala
import org.scalajs.dom

final class Test1 extends sjest.JestSuite {
  test("this is my first test") {
    println("do some test1!")
    expect(1 + 2).toBe(3)
  }

  test("this is a failed test") {
    expect(1 + 1).toBe(5)
  }

  test("dom test") {
    val div = dom.document.createElement("div")
    div.setAttribute("id","my-id-is-app")
    val body = dom.document.body.appendChild(div)
    val found = dom.document.getElementById("my-id-is-app")
    expect(found).toBe(div)
  }
}
```

You are good to go!

### Misc

* This project depends on [Nodejs binding](https://github.com/scalajs-io/nodejs).

* Use [Macwire](https://github.com/adamw/macwire) to manage dependency injection
 as well as parameters passed down to `Task` from `Framework`
 
### About
 
Author: Cause Chung (cuzfrog@139.com)
 
License: Apache License Version 2.0