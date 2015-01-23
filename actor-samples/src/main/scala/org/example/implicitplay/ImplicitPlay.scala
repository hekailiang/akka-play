package org.example.implicitplay

/**
 * Created by kailianghe on 1/22/15.
 */

trait Comparable[T] {
  def comp(o:T) : Int
}

class A

object B {
  implicit class ExA(a : A) extends Comparable[A] {
    override def comp(o: A): Int = a.hashCode - o.hashCode
  }
}

object C {
  implicit def int2A(v : Int) = new A
}

class D {
  def call[T : Comparable](a: T) = {
    // compiler give me the Comparable[T] instance defined in the context implicitly
    val cp = implicitly[Comparable[T]]
    cp.comp(a)
  }
}

object E {
  implicit val cp = new Comparable[A] {
    override def comp(o: A): Int = 123
  }
}

object ImplicitPlay extends App {
  val a = new A
  val b = new A

  // 1. extend a existing class A
  import org.example.implicitplay.B.ExA
  println(a.comp(b))

  // 2. auto convert parameter for me
  import org.example.implicitplay.C.int2A
  println(a.comp(1))

  // 3. find a implicit variable Comparable from context
  import org.example.implicitplay.E.cp
  println((new D).call(new A))
}