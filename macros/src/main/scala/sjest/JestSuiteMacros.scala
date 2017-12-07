package sjest

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

private object JestSuiteMacros {

  def describeMacro(c: whitebox.Context)
                   (description: c.Expr[String])(tests: c.Expr[Unit]): c.Tree = {
    tests.tree
  }
}
