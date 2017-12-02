package sjest.impl

import sbt.testing._

private case class JestTestEvent private(override val fullyQualifiedName: String,
                                       override val fingerprint: Fingerprint,
                                       override val selector: Selector,
                                       override val status: Status,
                                       override val throwable: OptionalThrowable,
                                       override val duration: Long
                                      ) extends sbt.testing.Event with Product with Serializable

private object JestTestEvent {
  def apply(status: Status,
            duration: Long = -1,
            throwable: OptionalThrowable = new OptionalThrowable())
           (implicit taskDef: TaskDef): JestTestEvent = {
    new JestTestEvent(
      taskDef.fullyQualifiedName(),
      taskDef.fingerprint(),
      taskDef.selectors().head,
      status,
      throwable,
      duration
    )
  }
}