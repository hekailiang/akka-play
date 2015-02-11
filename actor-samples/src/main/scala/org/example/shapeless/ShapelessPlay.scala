package org.example.shapeless

import shapeless._

/**
 * Created by kailianghe on 1/29/15.
 */

// size is a function from values of arbitrary type to a 'size' which is
// defined via type specific cases
object size extends Poly1 {
  implicit val caseInt = at[Int](x => 1)

  implicit val caseString = at[String](_.length)

  implicit def caseList[T] = at[List[T]](_.length)

  implicit def caseSet[T](implicit st : Pullback1[T, Int]) = at[Set[T]](t => {
    t map {size(_)} sum
  } )

  implicit def caseTuple[T, U](implicit st : Pullback1[T, Int], su : Pullback1[U, Int]) =
    at[(T, U)](t => size(t._1)+size(t._2))

  implicit def default[T] = at[T]( t => { println("matched default"); 1})

  implicit def caseOption[T](implicit st : Pullback1[T, Int]) =
    at[Option[T]](t => 1+(t map {size(_)}).getOrElse(0))
}

object bidi extends Poly1 {
  implicit val caseInt = at[Int](_.toString)
  implicit val caseString = at[String](_.toInt)
}

object ShapelessPlay extends App {
  println(size(Option("sadas")))

  val l = 23 :: true :: "foo" :: ("bar", "wibble") :: HNil
  val ls = l map size
  println(ls)

  val lis = 1 :: "2" :: 3 :: "4" :: HNil
  val lsi = lis map bidi

  println(lsi)
}
