package org.example.json

import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
 * Created by kailianghe on 1/26/15.
 */

case class Container[T](status: String, value : T)

object ExtraTypes {
  type ¬[A] = A => Nothing
  type ∨[T, U] = ¬[¬[T] with ¬[U]]
  type ¬¬[A] = ¬[¬[A]]
  type |∨|[T, U] = { type λ[X] = ¬¬[X] <:< (T ∨ U) }

  def size[T : (Int |∨| String)#λ](t : T) = t match {
    case i : Int => i
    case s : String => s.length
  }
}

case class NotFound() {
  def valueAtOneQuarter(f : Double => Double) = f(0.25)
}

object ContainerProtocol extends DefaultJsonProtocol {
  // "[T : JsonFormat]" used to wired in JsonFormat for generic type, in this case initialize tuple2Format defined
  // in StandardFormats
  implicit def containerFormat[T : JsonFormat] = jsonFormat2(Container[T])

  import ExtraTypes._
  case class IntOrStringTuple2[String, T : (Int |∨| String)#λ](val _1: String, val _2: T) extends Product2[String, T]

//  implicit def intOrStringTuple2[IntOrStringTuple2] = new JsonFormat[IntOrStringTuple2] {
//    override def write(obj: IntOrStringTuple2): JsValue = JsString("_NULL_")
//
//    override def read(json: JsValue): IntOrStringTuple2 = null.asInstanceOf[IntOrStringTuple2]
//  }
}

import spray.json._

object GenericJsonApp extends App {
  import ContainerProtocol._
//  val c = Container(
//    "OK", List( IntOrStringTuple2("Result", "Hello World!"), IntOrStringTuple2("Value", 123) )
//  );

//  val err = Container("Err", NotFound())
  val c = Container( "OK", List( ("Result", "Hello World"), ("Result2", "Hello World2")) );
  println(c.toJson)
}
