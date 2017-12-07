package object sjest {
  private[sjest] def NEWLINE: String = NEWLINE()
  private[sjest] def NEWLINE(n: Int = 1): String = System.lineSeparator() * n
}
