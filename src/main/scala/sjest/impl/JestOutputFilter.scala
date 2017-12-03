package sjest.impl

import sjest.NEWLINE

private[sjest] sealed trait JestOutputFilter extends Function1[String,String]{
  def apply(in: String): String
}

private object JestOutputFilter{
  implicit val defaultImpl: JestOutputFilter = new JestOutputFilterDefaultImpl
}

private final class JestOutputFilterDefaultImpl extends JestOutputFilter {
  private final val ansiRegex = """[\u001b\u009b][[()#;?]*(?:[0-9]{1,4}(?:;[0-9]{0,4})*)?[0-9A-ORZcf-nqry=><]"""

  def apply(in: String): String = {
    val lines = in.replaceAll(ansiRegex, "").split(raw"""$NEWLINE""")
    val filtered = lines.filter { line =>
      line.trim.nonEmpty &&
        !line.startsWith(",[BABEL] Note: The code generator has deoptimised the styling of") &&
        !line.startsWith("Ran all test suites matching") &&
        !line.startsWith("Test Suites: 1 passed, 1 total")
    }
    filtered.mkString(NEWLINE)
  }
}
