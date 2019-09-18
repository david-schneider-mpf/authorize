import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

object Main {
  def main(args: Array[String]) {

    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route =
      path("authorize") {
        headerValueByName("Authorization") {
          token =>
            println("token: " + token)
            get {
              parameters(Symbol("path"), Symbol("method"), Symbol("ownerId")) { (path, method, ownerId) =>
                if (System.currentTimeMillis() % 2 == 0) {
                  println("authorizing: path = " + path + "; method = " + method + "; ownerId = " + ownerId)
                  complete(StatusCodes.OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, "query_filters: owner_id in (1, 2, 3);column_filters: password"))
                } else {
                  println("not authorizing: path = " + path + "; method = " + method + "; ownerId = " + ownerId)
                  complete(StatusCodes.Forbidden, HttpEntity(ContentTypes.`text/plain(UTF-8)`, "not authorized: path = " + path + "; method = " + method + "; ownerId = " + ownerId))
                }
              }
            }
        }
      } ~ path("dump") {
        extractRequest {
          request =>
            get {
              request.headers.foreach(h => println(h.name() + ": " + h.value()))
              complete(StatusCodes.OK)
            }
        }
      } ~ path("ownership") {
        post {
          entity(as[String]) {
            m =>
              println(m)
              complete(StatusCodes.Created)
          }
        }
      } ~ path("create") {
        post {
          respondWithHeader(Location(Uri("http://new.resource.com"))) {
            complete(StatusCodes.Created)
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  }

}