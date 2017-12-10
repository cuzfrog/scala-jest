credentials ++= {
  val bintrayUser = System.getenv("BINTRAY_USER")
  val bintrayPass = System.getenv("BINTRAY_PASS")
  if (bintrayUser == null || bintrayPass == null) Nil
  else Seq(Credentials("Bintray API Realm", "api.bintray.com", bintrayUser, bintrayPass))
}

credentials ++= {
  val user = System.getenv("SONATYPE_USER")
  val password = System.getenv("SONATYPE_PASS")
  if (user == null || password == null) Nil
  else Seq(
    Credentials("Sonatype Nexus Repository Manager",
      "oss.sonatype.org", user, password)
  )
}