package sjest.support

import scala.scalajs.reflect.Reflect

private[sjest] object Utils {
  def loadModule[T](fqcn: String):T = {
    val moduleName = if (fqcn.endsWith("$")) fqcn else fqcn + "$"
    Reflect.lookupLoadableModuleClass(moduleName) match {
      case Some(moduleClass) => moduleClass.loadModule().asInstanceOf[T]
      case None => throw new ClassNotFoundException(fqcn)
    }
  }
}
