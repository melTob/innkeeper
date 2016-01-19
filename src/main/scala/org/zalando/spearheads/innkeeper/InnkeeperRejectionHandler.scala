package org.zalando.spearheads.innkeeper

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ AuthenticationFailedRejection, AuthorizationFailedRejection, MethodRejection, RejectionHandler }
import org.zalando.spearheads.innkeeper.api.Error
import org.zalando.spearheads.innkeeper.api.JsonProtocols.errorFormat
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
/**
 * @author dpersa
 */

object InnkeeperRejectionHandler {

  implicit def rejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case AuthorizationFailedRejection => complete(
          StatusCodes.Unauthorized,
          Error(StatusCodes.Unauthorized.intValue, "Authorization failed", "AUTH1")
        )
        case AuthenticationFailedRejection(_, _) => complete(
          StatusCodes.Unauthorized,
          Error(StatusCodes.Unauthorized.intValue, "Authentication failed", "AUTH2")
        )
        case RouteNotFoundRejection =>
          complete(
            StatusCodes.NotFound,
            Error(StatusCodes.NotFound.intValue, "Resource not found", "RNF")
          )

        case InvalidRouteNameRejection =>
          complete(
            StatusCodes.BadRequest,
            Error(StatusCodes.BadRequest.intValue, """"Invalid route name. Route names follow the pattern: "[a-zA-Z][a-zA-Z0-9_]*"""", "IRN")
          )
        case UnmarshallRejection => complete(
          StatusCodes.BadRequest,
          Error(StatusCodes.BadRequest.intValue, "Failed to unmarshall route", "UNMAR1")
        )
      }.handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        complete(
          StatusCodes.MethodNotAllowed,
          Error(StatusCodes.MethodNotAllowed.intValue,
            s"Method not allowed! Supported: ${names mkString " or "}!",
            "MNA")
        )
      }.handleNotFound {
        complete(
          StatusCodes.NotFound,
          Error(StatusCodes.NotFound.intValue, "Resource not found", "RNF")
        )
      }.result()
}
