import Settings._

inThisBuild(Seq(
  shellPrompt := { state => Project.extract(state).currentRef.project + "> " },
  scalaVersion := "2.12.3",
  crossScalaVersions := Seq("2.11.11", "2.12.3"),
  version := "0.0.1-SNAPSHOT"
))

val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings, publicationSettings, readmeVersionSettings)
  .settings(
    name := "simple-jest",
    libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-test-interface" % scalaJSVersion,
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided,
      "io.scalajs" %%% "nodejs" % "0.4.2",
      "com.lihaoyi" %%% "fansi" % "0.2.5",
      "com.lihaoyi" %%% "utest" % "0.6.0" % Test
    ),
    testFrameworks += new TestFramework("sjest.MyUTestFramework")
  )

val tests = project.dependsOn(root)
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "simple-jest-tests",
    libraryDependencies ++= Seq(
    ),
    testFrameworks += new TestFramework("anywhere.MyTestFramework")
  )

//mount tmpfs:
onLoad in Global := {
  val insertCommand: State => State =
    (state: State) =>
      state.copy(remainingCommands = ";root/tmpfsOn;tests/tmpfsOn" +: state.remainingCommands)
  (onLoad in Global).value andThen insertCommand
}