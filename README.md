# Scala-jest
[![Build Status](https://travis-ci.org/cuzfrog/scala-jest.svg?branch=master)](https://travis-ci.org/cuzfrog/scala-jest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.cuzfrog/sjest/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.cuzfrog/sjest)

Write `jest` tests as scala classes and run them within sbt without any hassle.

Build against scala-2.12 and scalajs-0.6.21.

### Motivation
* To workaround scala.js issue [#2635](https://github.com/scala-js/scala-js/issues/2635).
* To provide a better test ability against nodejs + jsdom + CommonJS on scala.js.
* That is: test your scala.js-react project just by `sbt test`

![scala-jest-demo.png](demo-pic/scala-jest-demo.png)

### Setup

Prerequisites:

* `nodejs` (probably higher than v8.7.0) 
* `package.json`, or `npm init` to make one
* `npm install --save-dev jest-cli`

_See scala.js doc for file path conventions._

Dependency:

    libraryDependencies += "com.github.cuzfrog" %%% "sjest" % "0.1.0-SNAPSHOT" % Test

Provide test Framework(to specify client build info):
```scala
val myTestFramework: TestFramework = new TestFramework("sjest.JestFramework")
testFrameworks += myTestFramework
testOptions += Tests.Argument(myTestFramework,
  s"-opt.js.path:${(artifactPath in Test in fastOptJS).value}")
```
Scala-jest has to know where the `*.opt.js` file is.
This can also be set through overriding `optJsPath` in `JestFramework` (see below).

### It's simple to use:

Write dom test:

```scala
import org.scalajs.dom

object MyTest extends sjest.JestSuite {
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

Write test with shared local variable:

```scala
object MyTestWithSharedVariable extends sjest.JestSuite{
  private var marker: Int = 0

  beforeEach {
    marker += 1
  }
  
  describe("outer1") {
    describe("inner2") {
      test("test2-1") {
        marker += 1
        println("do some test2-1!")
      }
    }
    test("test2-3") {
      marker += 1
      println("do some test2-3!")
    }
  }
    
  afterAll {
    println(marker) //4
    //Note: assertion here is useless, error here simply gets ignored by jest.
  }
}
```

Then sbt `test` or `testOnly` like you do in any other scala test framework.

_You can pass `jest` configs directly from console:_

    test -- --bail --verbose

### More config:

See more configs in [JestFramework](src/main/scala/sjest/JestFramework.scala)
```scala
class JestFramework {
  /** *opt.js full path or path relative to sbt root dir. This is prior to args */
  protected def optJsPath: String = ""
  /** Generated *.test.js full path or path relative to sbt root dir. */
  protected def testJsDir: String = "./target/scala-jests/"

  /** Yield test command, given a test.js file path. Jest configs can be put here. */
  protected def nodejsCmd(jsTestPath: String): NodejsCmd = (jsTestPath: String) =>
     NodejsCmd("node_modules/jest/bin/jest.js", js.Array("--colors", "--bail", jsTestPath))
  
  /** Whether to run actual test by `'sbt test'`.
   * 'jest xx.test.js' is executed for every test, thus worse performance.
   * One could disable it by set this to false, and manually run jest from command line.
   */
  protected def autoRunTestInSbt: Boolean = true
  
  /** Filter jest output in sbt console */
  protected def jestOutputFilter: String => String = defaultFilter
}
```

### Global life cycle:

#### How sjest works?
Scala-jest exposes client tests in `*opt.js`, and generates `*.test.js` files to call them,
executed by nodejs' `child_process`, which then triggers [jest](https://facebook.github.io/jest).
So scala.js is not involved the when testing is running, only the stdout/err shown in sbt console.

A problem is how to share objects/instances between these processes? Like:
```scala
object Global{
  var shared_in_multiple_TestSuites = ??? //not working...
}
```
The `Global` object is in limit scope as every test suite is executed in its own process.
Unfortunately, `jest`ing all `*.test.js` at once is not working too.

#### Global state

Communication between sub-processes is used to realize inter-test-suite sharing(not implemented yet).
This also brings the ability to do global setup/teardown.
```scala
class JestFramework {
  /** Setup before run */
  protected def beforeGlobal(): Any = ()
  /** Teardown after run */
  protected def afterGlobal(stub: Any): Any = stub
}
```
The result returned from `beforeGlobal` will be passed as a _stub_ to `afterGlobal`.
 
### About

Jest facade is from [scalajs-jest](https://github.com/scalajs-jest/core)
 
Author: Cause Chung (cuzfrog@139.com)
 
License: Apache License Version 2.0
