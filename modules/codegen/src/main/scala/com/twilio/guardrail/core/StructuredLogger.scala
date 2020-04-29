package com.twilio.guardrail.core

import cats.data.{ Chain, NonEmptyChain }
import cats.implicits._
import cats.{ Monoid, Order, Show }
import org.slf4j.LoggerFactory

sealed abstract class LogLevel(val level: String)
object LogLevel {
  implicit object ShowLogLevel extends Show[LogLevel] {
    def show(x: LogLevel): String = x match {
      case LogLevels.Debug   => "  DEBUG"
      case LogLevels.Info    => "   INFO"
      case LogLevels.Warning => "WARNING"
      case LogLevels.Error   => "  ERROR"
      case LogLevels.Silent  => " SILENT"
    }
  }

  implicit object OrderLogLevel extends Order[LogLevel] {
    def compare(l: LogLevel, r: LogLevel): Int =
      LogLevels.members.indexOf(l) - LogLevels.members.indexOf(r)
  }
}

object LogLevels {
  case object Debug   extends LogLevel("debug")
  case object Info    extends LogLevel("info")
  case object Warning extends LogLevel("warning")
  case object Error   extends LogLevel("error")
  case object Silent  extends LogLevel("silent")

  val members: Vector[LogLevel] = Vector(Debug, Info, Warning, Error, Silent)

  def apply(value: String): Option[LogLevel] = members.find(_.level == value)
}

sealed trait StructuredLogEntry
sealed case class StructuredLogBlock(lines: NonEmptyChain[(LogLevel, String)]) extends StructuredLogEntry
sealed case class StructuredLoggerPush(next: String)                           extends StructuredLogEntry
case object StructuredLoggerPop                                                extends StructuredLogEntry
case object StructuredLoggerReset                                              extends StructuredLogEntry

case class StructuredLogger(entries: Chain[StructuredLogEntry]) {
  @deprecated("0.58.0", "StructuredLogger has moved to slf4j! Please use StructuredLogger.toLogger(...) instead of .show")
  def show(implicit desiredLevel: LogLevel): String = {
    StructuredLogger.toLogger(this)
    ""
  }
}

object StructuredLogger extends StructuredLoggerInstances {
  def push(next: String): StructuredLogger = StructuredLogger(StructuredLoggerPush(next).pure[Chain])
  def pop: StructuredLogger                = StructuredLogger(StructuredLoggerPop.pure[Chain])
  def reset: StructuredLogger              = StructuredLogger(StructuredLoggerReset.pure[Chain])
  object Empty extends StructuredLogger(Chain.empty)

  def toLogger(value: StructuredLogger): Unit = {
    val _ = value.entries
      .foldLeft(Chain.empty[String])({
        case (newHistory, StructuredLoggerPop) =>
          newHistory.initLast.fold[Chain[String]](Chain.empty)(_._1)
        case (newHistory, StructuredLoggerPush(name)) =>
          newHistory :+ name
        case (newHistory, StructuredLoggerReset) =>
          Chain.empty
        case (newHistory, StructuredLogBlock(lines)) =>
          lines
            .toChain
            .toVector
            .foreach({
              case (level, message) =>
                val history = ("com.twilio.guardrail.interp" +: newHistory.toVector).mkString(".")
                val logger = LoggerFactory.getLogger(history)
                val emitter: String => Unit = level match {
                  case LogLevels.Debug   => logger.debug(_: String)
                  case LogLevels.Info    => logger.info(_: String)
                  case LogLevels.Warning => logger.warn(_: String)
                  case LogLevels.Error   => logger.error(_: String)
                  case LogLevels.Silent  => (_: String) => ()
                }
                emitter(message)
            })
          newHistory
      })
  }
}

sealed trait StructuredLoggerInstances {
  implicit object StructuredLoggerMonoid extends Monoid[StructuredLogger] {
    def empty: StructuredLogger                                             = StructuredLogger(Chain.empty)
    def combine(x: StructuredLogger, y: StructuredLogger): StructuredLogger = StructuredLogger(Monoid[Chain[StructuredLogEntry]].combine(x.entries, y.entries))
  }

  def debug(message: String): StructuredLogger =
    StructuredLogger(Chain(StructuredLogBlock(NonEmptyChain.one((LogLevels.Debug, message)))))
  def info(message: String): StructuredLogger =
    StructuredLogger(Chain(StructuredLogBlock(NonEmptyChain.one((LogLevels.Info, message)))))
  def warning(message: String): StructuredLogger =
    StructuredLogger(Chain(StructuredLogBlock(NonEmptyChain.one((LogLevels.Warning, message)))))
  def error(message: String): StructuredLogger =
    StructuredLogger(Chain(StructuredLogBlock(NonEmptyChain.one((LogLevels.Error, message)))))
}
