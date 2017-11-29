import sbt.Keys._
import sbt._
import MyTasks._

object Settings {
  val commonSettings = Seq(
    resolvers ++= Seq(
      Resolver.bintrayRepo("cuzfrog","maven")
    ),
    organization := "com.github.cuzfrog",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"),
    libraryDependencies ++= Seq(

    ),
    logBuffered in Test := false,
    parallelExecution in Test := false,
    licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))
  )

  val publicationSettings = Seq(
    publishTo := Some("My Bintray" at s"https://api.bintray.com/maven/cuzfrog/maven/${name.value }/;publish=1")
  )

  val readmeVersionSettings = Seq(
    (compile in Compile) := ((compile in Compile) dependsOn versionReadme).value,
    versionReadme := {
      val contents = IO.read(file("README.md"))
      val regex =raw"""(?<=libraryDependencies \+= "com\.github\.cuzfrog" %% "${name.value}" % ")[\d\w\-\.]+(?=")"""
      val newContents = contents.replaceAll(regex, version.value)
      IO.write(file("README.md"), newContents)
    }
  )
}