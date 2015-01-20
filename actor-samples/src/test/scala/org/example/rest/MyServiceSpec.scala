package org.example.rest

import org.scalatest.{Matchers, FreeSpec}
import spray.testkit.ScalatestRouteTest

/**
 * Created by kailianghe on 1/20/15.
 */
class MyServiceSpec extends FreeSpec with MyService with ScalatestRouteTest with Matchers {

  def actorRefFactory = system

  "The MyService" - {
    "when calling GET" - {
      "should return a greeting for GET requests to the root path" in {
        Get() ~> myRoute ~> check {
          responseAs[String] should include("Say hello")
        }
      }
    }
  }

}
