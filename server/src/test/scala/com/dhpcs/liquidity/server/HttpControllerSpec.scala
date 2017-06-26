package com.dhpcs.liquidity.server

import java.net.InetAddress

import akka.NotUsed
import akka.actor.ActorPath
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.headers.`Remote-Address`
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.model.{ContentType, RemoteAddress, StatusCodes}
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.stream.scaladsl.Flow
import com.dhpcs.liquidity.model.{AccountId, PublicKey, Zone, ZoneId}
import com.typesafe.config.{Config, ConfigFactory}
import org.json4s.JValue
import org.json4s.JsonAST.{JInt, JObject}
import org.json4s.jackson.JsonMethods
import org.scalatest.{FreeSpec, Inside}

import scala.concurrent.Future

class HttpControllerSpec extends FreeSpec with HttpController with ScalatestRouteTest with Inside {

  override def testConfig: Config = ConfigFactory.defaultReference()

  "The HttpController" - {
    "will provide version information" in {
      val getRequest = RequestBuilding.Get("/version")
      getRequest ~> httpRoutes(enableClientRelay = true) ~> check {
        assert(status === StatusCodes.OK)
        assert(contentType === ContentType(`application/json`))
        inside(JsonMethods.parse(entityAs[String])) {
          case JObject(obj) =>
            val keys = obj.toMap.keySet
            assert(keys.contains("version"))
            assert(keys.contains("builtAtString"))
            assert(keys.contains("builtAtMillis"))
        }
      }
    }
    "will accept WebSocket connections" in {
      val wsProbe = WSProbe()
      WS("/ws", wsProbe.flow)
        .addHeader(
          `Remote-Address`(RemoteAddress(InetAddress.getLoopbackAddress))
        ) ~> httpRoutes(enableClientRelay = true) ~> check {
        assert(isWebSocketUpgrade === true)
        val message = "Hello"
        wsProbe.sendMessage(message)
        wsProbe.expectMessage(message)
        wsProbe.sendCompletion()
        wsProbe.expectCompletion()
      }
    }
    "will provide status information" in {
      val getRequest = RequestBuilding.Get("/status")
      getRequest ~> httpRoutes(enableClientRelay = true) ~> check {
        assert(status === StatusCodes.OK)
        assert(contentType === ContentType(`application/json`))
        assert(
          JsonMethods.parse(entityAs[String]) === JObject(
            "clients"         -> JObject(),
            "totalZonesCount" -> JInt(0),
            "activeZones"     -> JObject(),
            "shardRegions"    -> JObject(),
            "clusterSharding" -> JObject()
          ))
      }
    }
  }

  override protected[this] def webSocketApi(ip: RemoteAddress): Flow[Message, Message, NotUsed] =
    Flow[Message]

  override protected[this] def getStatus: Future[JValue] =
    Future.successful(
      JObject(
        "clients"         -> JObject(),
        "totalZonesCount" -> JInt(0),
        "activeZones"     -> JObject(),
        "shardRegions"    -> JObject(),
        "clusterSharding" -> JObject()
      ))
  override protected[this] def getZone(zoneId: ZoneId): Future[Option[Zone]] = Future.successful(None)
  override protected[this] def getBalances(zoneId: ZoneId): Future[Map[AccountId, BigDecimal]] =
    Future.successful(Map.empty)
  override protected[this] def getClients(zoneId: ZoneId): Future[Map[ActorPath, (Long, PublicKey)]] =
    Future.successful(Map.empty)

}
