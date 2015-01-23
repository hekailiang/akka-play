package org.example.json

import spray.json._

/**
 * http://www.cakesolutions.net/teamblogs/2012/11/30/spray-json-and-adts
 * In this post, I'll show you how to serialize (and deserialize) complex hierarchies of objects in Spray JSON. We'll be
 * dealing with ever so slightly complex tree structure. You'll find out how to go beyond the JsonFormat[A] instances
 * you get from calling jsonFormatn.
 */

/**
 * Let's first begin by defining our tree. We'll be modelling forms; a form contains fields or other forms as sub-forms.
 * Both the form and the field need a label and an optional hint element. The field element needs to know what type of
 * value it will hold (boolean, number, date, ...). In code, this is quite nice structure:
 */
sealed trait Element {
  def label: String
  def hint: Option[String]
}

/**
 * This defines the base type Element that every node in our tree will share. Moving on, let's outline the various kinds
 * of fields we intend to support.
 */
sealed trait FieldElementKind
case object BooleanFieldElementKind extends FieldElementKind
case object StringFieldElementKind  extends FieldElementKind
case object DateFieldElementKind    extends FieldElementKind
case object NumberFieldElementKind  extends FieldElementKind

/**
 * We can now bring everything together, defining the FieldElement as well as the FormElement like so:
 */
sealed trait FieldElement extends Element
case class ScalarFieldElement( label: String,
                               kind: FieldElementKind,
                               hint: Option[String] ) extends FieldElement

case class FormElement( label: String,
                        hint: Option[String],
                        elements: List[Element] ) extends Element


case class Customer(firstName: String,
                    lastName: String,
                    id: Option[String] = None,
                    phoneNumber: Option[String] = None,
                    address: Option[String] = None,
                    city: Option[String] = Some("New York"),
                    country: Option[String] = Some("USA"),
                    zipCode: Option[String] = None) {
}

object FormDefinitionFormats extends DefaultJsonProtocol {

  // we write the kinds as JsStrings of their values
  implicit object ElementKindFormat extends JsonFormat[FieldElementKind] {
    def write(obj: FieldElementKind) = JsString(obj.toString)
    def read(json: JsValue): FieldElementKind = json match {
      case JsString("BooleanFieldElementKind") => BooleanFieldElementKind
      case JsString("NumberFieldElementKind")  => NumberFieldElementKind
      case JsString("StringFieldElementKind")  => StringFieldElementKind
      case JsString("DateFieldElementKind")    => DateFieldElementKind
      case _ => throw new RuntimeException
    }
  }

  // we wrap every Element in document containing its kind and the
  // underlying value
  implicit def elementFormat: JsonFormat[Element] = new JsonFormat[Element] {
    def write(obj: Element) = obj match {
      case e: ScalarFieldElement =>
        JsObject(
          ("kind", JsString("scalar")),
          ("element", ScalarFieldElementFormat.write(e))
        )
      case e: FormElement        =>
        JsObject(
          ("kind", JsString("form")),
          ("element", FormElementFormat.write(e))
        )
    }

    def read(json: JsValue) = json match {
      case JsObject(fields) =>
        (fields("kind"), fields("element")) match {
          case (JsString("scalar"), element) =>
            ScalarFieldElementFormat.read(element)
          case (JsString("form"),   element) =>
            FormElementFormat.read(element)
          case _ => throw new RuntimeException
        }

      case _ => throw new RuntimeException
    }
  }

  implicit val ScalarFieldElementFormat = jsonFormat3(ScalarFieldElement)
  implicit val FormElementFormat = jsonFormat3(FormElement)
}

object SprayJsonPlay extends App {
  /**
   * So, having a form: FormElement allows us to encode a form with any number of fields and any number of sub-forms and
   * so on, recursively. Now, imagine that we have to turn this structure into JSON. Suppose our form instance is:
   */
  val e = FormElement("Top-level form", None, List(
      ScalarFieldElement("Field 1", NumberFieldElementKind, Some("Enter 42")),
      FormElement("Sub-form", None, List(
        ScalarFieldElement("Field 1.1", BooleanFieldElementKind, None) )
      )
    )
  )

  import FormDefinitionFormats._
  val json = e.toJson
  println(json.prettyPrint)

  val f = json.convertTo[FormElement]
  println(f==e)

}
