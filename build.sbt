import Settings._

shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }
scalaVersion in ThisBuild := "2.12.3"
crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.3")

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings, publicationSettings, readmeVersionSettings)
  .settings(
    name := "simple-jest",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-test-interface" % scalaJSVersion
    ),
    testFrameworks += new TestFramework("anywhere.MyTestFramework"),
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    tmpfsDirectoryMode := TmpfsDirectoryMode.Mount
  )

//mount tmpfs:
onLoad in Global := {
  val insertCommand: State => State =
    (state: State) =>
      state.copy(remainingCommands = ";tmpfsOn" +: state.remainingCommands)
  (onLoad in Global).value andThen insertCommand
}