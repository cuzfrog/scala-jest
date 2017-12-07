package sjest.support

import java.lang.annotation.Documented

import scala.annotation.Annotation

@Documented
private[sjest] final class SideEffect[T](affected: T*) extends Annotation

@Documented
private[sjest] final class Stateful extends Annotation

@Documented
private[sjest] final class Stateless extends Annotation

@Documented
private[sjest] final class ThreadSafe extends Annotation

@Documented
private[sjest] final class VisibleForTest extends Annotation

@Documented
private[sjest] final class Service extends Annotation

@Documented
private[sjest] final class InternalControl extends Annotation