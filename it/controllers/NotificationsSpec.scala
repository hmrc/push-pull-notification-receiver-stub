/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.WiremockSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.DefaultAwaitTimeout
import play.api.test.FutureAwaits

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import play.api.http.ContentTypes
import play.api.mvc.Headers
import play.api.http.HeaderNames

class NotificationsSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite with WiremockSuite with FutureAwaits with DefaultAwaitTimeout {
  override protected def portConfigKeys: Seq[String] = Seq.empty

  lazy val ws: WSClient = app.injector.instanceOf[WSClient]

  "GET /notifications" should {
    "return OK" in {
      await(
        ws
          .url(s"http://localhost:$port/push-pull-notification-receiver-stub/notifications")
          .get()
      ).status shouldBe Status.OK
    }
  }

  "POST /notifications" should {
    "return ACCEPTED for a JSON message" in {
      await(
        ws
          .url(s"http://localhost:$port/push-pull-notification-receiver-stub/notifications")
          .withHttpHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
          .post(
            Json.stringify(
              Json.obj(
                "notificationId" -> UUID.randomUUID.toString,
                "boxId" -> UUID.randomUUID.toString,
                "status" -> "ACKNOWLEDGED",
                "messageContentType" -> "application/json",
                "message" -> """{"key":"value"}""",
                "createdDateTime" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now)
              )
            )
          )
      ).status shouldBe Status.ACCEPTED
    }

    "return ACCEPTED for an XML message" in {
      await(
        ws
          .url(s"http://localhost:$port/push-pull-notification-receiver-stub/notifications")
          .withHttpHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
          .post(
            Json.stringify(
              Json.obj(
                "notificationId" -> UUID.randomUUID.toString,
                "boxId" -> UUID.randomUUID.toString,
                "status" -> "ACKNOWLEDGED",
                "messageContentType" -> "application/xml",
                "message" -> s"""
                |<?xml version="1.0" encoding="UTF-8"?>
                |<CC007A>
                |  <SynIdeMES1>UNOC</SynIdeMES1>
                |  <SynVerNumMES2>3</SynVerNumMES2>
                |  <MesRecMES6>NCTS</MesRecMES6>
                |  <DatOfPreMES9>20200519</DatOfPreMES9>
                |  <TimOfPreMES10>1357</TimOfPreMES10>
                |  <IntConRefMES11>WE190912102534</IntConRefMES11>
                |  <AppRefMES14>NCTS</AppRefMES14>
                |  <TesIndMES18>0</TesIndMES18>
                |  <MesIdeMES19>1</MesIdeMES19>
                |  <MesTypMES20>GB007A</MesTypMES20>
                |  <HEAHEA>
                |    <DocNumHEA5>01CTC201909121215</DocNumHEA5>
                |    <ArrNotPlaHEA60>DOVER</ArrNotPlaHEA60>
                |    <ArrNotPlaHEA60LNG>EN</ArrNotPlaHEA60LNG>
                |    <ArrAgrLocOfGooHEA63LNG>EN</ArrAgrLocOfGooHEA63LNG>
                |    <SimProFlaHEA132>1</SimProFlaHEA132>
                |    <ArrNotDatHEA141>20190912</ArrNotDatHEA141>
                |    <DiaLanIndAtDesHEA255>EN</DiaLanIndAtDesHEA255>
                |  </HEAHEA>
                |  <TRADESTRD>
                |    <CouTRD25>GB</CouTRD25>
                |    <NADLNGRD>EN</NADLNGRD>
                |    <TINTRD59>GB602070107000</TINTRD59>
                |  </TRADESTRD>
                |  <CUSOFFPREOFFRES>
                |    <RefNumRES1>GB000060</RefNumRES1>
                |  </CUSOFFPREOFFRES>
                |</CC007A>
                |""".trim.stripMargin,
                "createdDateTime" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now)
              )
            )
          )
      ).status shouldBe Status.ACCEPTED
    }

    "return BAD_REQUEST for a JSON message with XML content type" in {
      await(
        ws
          .url(s"http://localhost:$port/push-pull-notification-receiver-stub/notifications")
          .withHttpHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
          .post(
            Json.stringify(
              Json.obj(
                "notificationId" -> UUID.randomUUID.toString,
                "boxId" -> UUID.randomUUID.toString,
                "status" -> "ACKNOWLEDGED",
                "messageContentType" -> "application/xml",
                "message" -> """{"key":"value"}""",
                "createdDateTime" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now)
              )
            )
          )
      ).status shouldBe Status.BAD_REQUEST
    }
  }
}
