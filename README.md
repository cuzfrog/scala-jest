# Simple-jest
Write `jest` tests as scala classes and run them within sbt without any hassle.

### Motivation
To workaround scala.js issue #2635.
To provide a better test ability against nodejs + jsdom + CommonJS on scala.js.

### How it works?
Simple-jest does not run tests within sbt. It exposes client tests in `*opt.js`,
generates `*.test.js` files to call them, and uses nodejs' `child_process` to execute command line
`npm test`, which then trigger `jest`. So scala.js is not involved in the testing process,
only the stdout/err from npm shown in sbt console.

A drawback is that file paths are not managed by sbt, and must be set manually.

### Setup

Prerequisite:

* nodejs (probably higher than v7.5.0) 
* a `package.json`, or `npm init` to make one
* `npm install --save-dev jest`

_See scala.js doc for file path conventions._

Dependency:

    libraryDependencies += "com.github.cuzfrog" %%% "simple-jest" % "0.0.1-SNAPSHOT" Test
    
### How to use: