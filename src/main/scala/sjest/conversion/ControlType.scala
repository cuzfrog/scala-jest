package sjest.conversion

import enumeratum._

import scala.collection.immutable

private[sjest] sealed trait ControlType extends EnumEntry{
  final def decapitalizedString: String = ControlType.decapitalize(this.toString)
}

private[sjest] object ControlType extends Enum[ControlType]{
  case object BeforeAll extends ControlType
  case object BeforeEach extends ControlType
  case object AfterAll extends ControlType
  case object AfterEach extends ControlType

  //from https://stackoverflow.com/questions/4052840/most-efficient-way-to-make-the-first-character-of-a-string-lower-case
  private final def decapitalize(string: String): String = {
    if (string == null || string.length() == 0) {
      string
    } else {
      val c = string.toCharArray
      c.update(0, Character.toLowerCase(c(0)))
      String.valueOf(c)
    }
  }
  override def values: immutable.IndexedSeq[ControlType] = findValues
}