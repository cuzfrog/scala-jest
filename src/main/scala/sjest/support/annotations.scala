package sjest.support

import java.lang.annotation.Documented

@Documented
private[sjest] final class SideEffect[T](affected: T*) extends scala.annotation.Annotation

@Documented
private[sjest] final class Stateful extends scala.annotation.Annotation

@Documented
private[sjest] final class Stateless extends scala.annotation.Annotation

@Documented
private[sjest] final class VisibleForTest extends scala.annotation.Annotation