package org.example.message

/**
 * Created by kailianghe on 1/8/15.
 */
object TeacherProtocol {
  case class QuoteRequest()
  case class QuoteResponse(quoteString:String)
  case class InitSignal()
}
