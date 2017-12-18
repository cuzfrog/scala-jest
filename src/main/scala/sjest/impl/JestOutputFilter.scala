package sjest.impl

import sjest.support.Stateless

import scala.annotation.tailrec

@Stateless
private sealed trait JestOutputFilter extends Function1[String, String] {
  def apply(in: String): String
}

private object JestOutputFilter {
  val instance: JestOutputFilter = new JestOutputFilterDefaultImpl
}

private final class JestOutputFilterDefaultImpl extends JestOutputFilter {
  private final val ansiRegex = fansi.Str.ansiRegex.pattern()

  def apply(in: String): String = {

    val originalLines = in.split(raw"""$NEWLINE""").map(removePrefixComma)
    val noAnsiLines = originalLines.map(_.replaceAll(ansiRegex, ""))

    val lines = originalLines zip noAnsiLines

    val filtered = lines.collect { case (original, line)
      if line.trim.nonEmpty &&
        !line.startsWith("[BABEL] Note: The code generator has deoptimised the styling of") &&
        !line.startsWith("Ran all test suites matching") &&
        !line.startsWith("Tests:") &&
        !line.startsWith("Test Suites:") &&
        !line.startsWith("Snapshots:") &&
        !line.startsWith("""Time:""") &&
        !(line.trim.startsWith("at") && line.contains("scala/scala/scalajs/runtime/AnonFunctions.scala")) =>
      original
    }
    filtered.mkString(NEWLINE)
  }

  @tailrec
  private def removePrefixComma(line: String): String = {
    if (line.startsWith(",")) removePrefixComma(line.drop(1))
    else line
  }
}
