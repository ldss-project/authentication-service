package io.github.jahrim.chess.authentication.service.components.adapters.http

class AuthenticationHttpAdapter {}

//class AuthenticationAdapterHttp extends Adapter[AuthenticationPort]:
//  override protected def init(context: AdapterContext[AuthenticationPort]): Unit =
//    val router = Router.router(context.vertx)
//
//    router.get("/").handler { message =>
//      context.log.info("Welcome!")
//      message.response().send("Ciao, questo e' l'Authentication Service...")
//    }
//    router.post("/user/:username").handler { message =>
//      context.api.registerUser("paolo", "paolo@gmail.com", "1234")
//    }
//    context.vertx
//      .createHttpServer()
//      .requestHandler(router)
//      .listen(8080, server => context.log.info("The server is up!"))
