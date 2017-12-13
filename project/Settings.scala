import sbt.Keys._
import sbt._
import MyTasks._
import com.github.cuzfrog.sbttmpfs.SbtTmpfsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
import com.typesafe.sbt.pgp.PgpKeys._

object Settings {
  val commonSettings = Seq(
    resolvers ++= Seq(
      Resolver.bintrayRepo("cuzfrog", "maven")
    ),
    organization := "com.github.cuzfrog",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-P:scalajs:sjsDefinedByDefault"),
    libraryDependencies ++= Seq(

    ),
    logBuffered in Test := false,
    parallelExecution in Test := false,
    licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0")),
    tmpfsDirectoryMode := TmpfsDirectoryMode.Mount,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  )

  val publicationSettings = Seq(
    publishConfiguration := withOverwrite(publishConfiguration.value, isSnapshot.value),
    publishSignedConfiguration := withOverwrite(publishSignedConfiguration.value, isSnapshot.value),
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    publishTo := Some(
      if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
      else Opts.resolver.sonatypeStaging
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/cuzfrog/scala-jest"),
        "scm:git@github.com:cuzfrog/scala-jest.git"
      )
    ),
    developers := List(
      Developer(id = "cuzfrog", name = "Cause Chung",
        email = "cuzfrog@139.com", url = url("https://github.com/cuzfrog/"))
    ),
    licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0")),
    homepage := Some(url("https://github.com/cuzfrog/scala-jest"))
  )

  val readmeVersionSettings = Seq(
    (compile in Compile) := ((compile in Compile) dependsOn versionReadme).value,
    versionReadme := {
      val contents = IO.read(file("README.md"))
      val regex =raw"""(?<=libraryDependencies \+= "com\.github\.cuzfrog" %%% "${name.value}" % ")[\d\w\-\.]+(?=" % Test)"""
      val newContents = contents.replaceAll(regex, version.value)
      IO.write(file("README.md"), newContents)
    }
  )

  private def withOverwrite(config: PublishConfiguration, isSnapshot: Boolean) = {
    config.withOverwrite(isSnapshot)
  }
}