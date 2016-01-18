package org.zalando.spearheads.innkeeper

import akka.http.scaladsl.model.StatusCodes
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import org.zalando.spearheads.innkeeper.AcceptanceSpecTokens.{INVALID_TOKEN, READ_TOKEN, WRITE_STRICT_TOKEN}
import org.zalando.spearheads.innkeeper.AcceptanceSpecsHelper._
import org.zalando.spearheads.innkeeper.RoutesRepoHelper.{recreateSchema, insertRoute}
import org.zalando.spearheads.innkeeper.api.RouteOut
import spray.json._
import spray.json.DefaultJsonProtocol._
import org.zalando.spearheads.innkeeper.api.JsonProtocols._


/**
  * @author dpersa
  */
class GetRoutesSpec extends FunSpec with BeforeAndAfter with Matchers {

  val routesRepo = RoutesRepoHelper.routesRepo

  describe("get /routes") {
    describe("success") {
      val token = READ_TOKEN

      before {
        recreateSchema
      }

      it("should get the routes") {
        insertRoute("R1")
        insertRoute("R2")

        val response = getSlashRoutes(token)
        response.status.shouldBe(StatusCodes.OK)
        val entity = entityString(response)
        val routes = entity.parseJson.convertTo[Seq[RouteOut]]
        routes.size should be(2)
      }
    }

    describe("failure") {
      describe("with an invalid token") {
        val token = INVALID_TOKEN

        it("should return the 401 Unauthorized status") {
          val response = getSlashRoutes(token)
          response.status.shouldBe(StatusCodes.Unauthorized)
        }
      }

      describe("with a token without the READ scope") {
        val token = WRITE_STRICT_TOKEN

        it("should return the 401 Unauthorized status") {
          val response = getSlashRoutes(token)
          response.status.shouldBe(StatusCodes.Unauthorized)
        }
      }
    }
  }
}
