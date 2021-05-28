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

import models.BoxId
import models.JsonNotification
import models.Notification
import models.NotificationId
import models.NotificationStatus
import models.XMLNotification
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers._

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class NotificationsControllerSpec extends AnyWordSpec with Matchers {

  private val controller = new NotificationsController(
    ???, // TODO: Write a fake NotificationService backed by a Map
    Helpers.stubControllerComponents()
  )

  "GET /notifications" should {
    "return OK" in {
      val fakeRequest = FakeRequest("GET", "/notification")
      val result      = controller.getNotifications(BoxId(UUID.randomUUID))(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsJson(result).as[Seq[Notification]] shouldBe Seq.empty
    }
  }

  "POST /notifications" should {
    "return ACCEPTED for a JSON notification" in {
      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      val fakeRequest =
        FakeRequest("POST", "/notifications").withBody(Json.toJsObject(notification))

      val result = controller.receiveNotification()(fakeRequest)

      status(result) shouldBe Status.ACCEPTED
    }

    "return ACCEPTED for an XML notification" in {
      val notification: Notification = XMLNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        <CC007A>
          <SynIdeMES1>UNOC</SynIdeMES1>
          <SynVerNumMES2>3</SynVerNumMES2>
          <MesRecMES6>NCTS</MesRecMES6>
          <DatOfPreMES9>20200519</DatOfPreMES9>
          <TimOfPreMES10>1357</TimOfPreMES10>
          <IntConRefMES11>WE190912102534</IntConRefMES11>
          <AppRefMES14>NCTS</AppRefMES14>
          <TesIndMES18>0</TesIndMES18>
          <MesIdeMES19>1</MesIdeMES19>
          <MesTypMES20>GB007A</MesTypMES20>
          <HEAHEA>
            <DocNumHEA5>01CTC201909121215</DocNumHEA5>
            <ArrNotPlaHEA60>DOVER</ArrNotPlaHEA60>
            <ArrNotPlaHEA60LNG>EN</ArrNotPlaHEA60LNG>
            <ArrAgrLocOfGooHEA63LNG>EN</ArrAgrLocOfGooHEA63LNG>
            <SimProFlaHEA132>1</SimProFlaHEA132>
            <ArrNotDatHEA141>20190912</ArrNotDatHEA141>
            <DiaLanIndAtDesHEA255>EN</DiaLanIndAtDesHEA255>
          </HEAHEA>
          <TRADESTRD>
            <CouTRD25>GB</CouTRD25>
            <NADLNGRD>EN</NADLNGRD>
            <TINTRD59>GB602070107000</TINTRD59>
          </TRADESTRD>
          <CUSOFFPREOFFRES>
            <RefNumRES1>GB000060</RefNumRES1>
          </CUSOFFPREOFFRES>
        </CC007A>,
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      val fakeRequest =
        FakeRequest("POST", "/notifications").withBody(Json.toJsObject(notification))

      val result = controller.receiveNotification()(fakeRequest)

      status(result) shouldBe Status.ACCEPTED
    }
  }
}
