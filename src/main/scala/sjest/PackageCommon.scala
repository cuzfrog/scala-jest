package sjest

private[sjest] trait PackageCommon {
  private[sjest] final def NEWLINE: String = NEWLINE()
  private[sjest] final def NEWLINE(n: Int = 1): String = System.lineSeparator() * n
}
