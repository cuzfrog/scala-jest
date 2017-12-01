package sjest

private class JestFingerprint extends sbt.testing.SubclassFingerprint {
  override def isModule(): Boolean = false
  override def superclassName(): String = "sjest.JestSuite"
  override def requireNoArgConstructor(): Boolean = true
}
