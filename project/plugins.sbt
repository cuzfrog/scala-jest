addSbtPlugin("com.github.cuzfrog" % "sbt-tmpfs" % "0.3.2")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

//addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.0.3") //not supported
//addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1") //not compatible

//release:
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

//scalajs
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")