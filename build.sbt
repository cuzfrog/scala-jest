import Settings._

inThisBuild(Seq(
  shellPrompt := { state => Project.extract(state).currentRef.project + "> " },
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4"), //"2.11.11" is not compatible with macwire
  version := "0.1.0"
))

//val macros = project
//  .enablePlugins(ScalaJSPlugin)
//  .settings(commonSettings)
//  .settings(
//    name := "sjest-macros",
//    libraryDependencies ++= Seq(
//      "org.scala-lang" % "scala-reflect" % scalaVersion.value
//    )
//  )

val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings, publicationSettings, readmeVersionSettings)
  .settings(
    name := "sjest",
    libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-test-interface" % scalaJSVersion,
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided,
      "io.scalajs" %%% "nodejs" % "0.4.2",
      "com.lihaoyi" %%% "fansi" % "0.2.5",
      "com.beachape" %%% "enumeratum" % "1.5.12",
      "com.lihaoyi" %%% "utest" % "0.6.0" % Test
    ),
    testFrameworks += new TestFramework("sjest.MyUTestFramework")
  )

val myTestFramework: TestFramework = new TestFramework("anywhere.MyTestFramework")
val tests = project.dependsOn(root)
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "sjest-tests",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.4"
    ),
    testFrameworks += myTestFramework,
    testOptions += Tests.Argument(myTestFramework,
      s"-opt.js.path:${(artifactPath in Test in fastOptJS).value}")
  )

//mount tmpfs:
onLoad in Global := {
  val insertCommand: State => State =
    (state: State) =>
      state.copy(remainingCommands = Exec(";root/tmpfsOn;tests/tmpfsOn", None) +: state.remainingCommands)
  (onLoad in Global).value andThen insertCommand
}

//release:
import ReleaseTransformations._

pgpReadOnly := false
pgpSecretRing := baseDirectory.value / "project" / "codesigning.asc"
pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray)
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  releaseStepCommand("pgp-cmd recv-key 895B79DB hkp://keyserver.ubuntu.com"),
  releaseStepCommand("+publishSigned"),
  releaseStepCommand("sonatypeReleaseAll")
)