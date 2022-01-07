/*
 * Copyright 2022 HM Revenue & Customs
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
import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers._
import services.FakeNotificationsService
import services.NotificationsService

import java.time.OffsetDateTime
import java.util.UUID
import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NotificationsControllerSpec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterEach
    with MockitoSugar {

  private val notificationsService = FakeNotificationsService(Map.empty)

  private val controller = new NotificationsController(
    notificationsService,
    Helpers.stubControllerComponents()
  )

  @nowarn
  override protected def beforeEach(): Unit = {
    await(notificationsService.deleteNotifications())
  }

  "GET /notifications" should {
    "return OK when there are no results" in {
      val fakeRequest = FakeRequest("GET", "/notifications")
      val result      = controller.getNotifications(BoxId(UUID.randomUUID))(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsJson(result).as[Seq[Notification]] shouldBe Seq.empty
    }

    "return OK when fetching a notification that was added" in {
      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      val fakePostRequest =
        FakeRequest("POST", "/notifications").withBody(Json.toJsObject(notification))

      val postResult = controller.receiveNotification()(fakePostRequest)

      status(postResult) shouldBe Status.OK

      val fakeGetRequest =
        FakeRequest("GET", s"/notifications/${notification.boxId.value}")

      val getResult = controller.getNotifications(notification.boxId)(fakeGetRequest)

      status(getResult) shouldBe Status.OK
      contentAsJson(getResult).as[Seq[Notification]] shouldBe Seq(notification)
    }

    "return INTERNAL_SERVER_ERROR when something goes wrong in the service" in {
      val mockNotificationsService = mock[NotificationsService]

      val controller = new NotificationsController(
        mockNotificationsService,
        Helpers.stubControllerComponents()
      )

      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      when(mockNotificationsService.saveNotification(notification))
        .thenReturn(Future.failed(new RuntimeException("Ruh roh")))

      val fakePostRequest =
        FakeRequest("POST", "/notifications").withBody(Json.toJsObject(notification))

      val postResult = controller.receiveNotification()(fakePostRequest)

      status(postResult) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "DELETE /notifications" should {
    "return ACCEPTED and delete all notifications" in {
      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      val fakePostRequest =
        FakeRequest("POST", "/notifications").withBody(Json.toJsObject(notification))

      val postResult = controller.receiveNotification()(fakePostRequest)

      status(postResult) shouldBe Status.OK

      val fakeGetRequest = FakeRequest("GET", s"/notifications/${notification.boxId.value}")

      val getResult = controller.getNotifications(notification.boxId)(fakeGetRequest)

      status(getResult) shouldBe Status.OK
      contentAsJson(getResult).as[Seq[Notification]] shouldBe Seq(notification)

      val fakeDeleteRequest = FakeRequest("DELETE", "/notifications")
      val deleteResult      = controller.deleteNotifications()(fakeDeleteRequest)
      status(deleteResult) shouldBe Status.ACCEPTED

      val getAfterDelete = controller.getNotifications(notification.boxId)(fakeGetRequest)
      status(getAfterDelete) shouldBe Status.OK
      contentAsJson(getAfterDelete).as[Seq[Notification]] shouldBe Seq.empty
    }

    "return INTERNAL_SERVER_ERROR if something goes wrong in the service" in {
      val mockNotificationsService = mock[NotificationsService]

      val controller = new NotificationsController(
        mockNotificationsService,
        Helpers.stubControllerComponents()
      )

      when(mockNotificationsService.deleteNotifications())
        .thenReturn(Future.failed(new RuntimeException("Ruh roh")))

      val fakeDeleteRequest = FakeRequest("DELETE", "/notifications")
      val deleteResult      = controller.deleteNotifications()(fakeDeleteRequest)

      status(deleteResult) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "POST /notifications" should {
    "return OK when adding a JSON notification" in {
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

      status(result) shouldBe Status.OK
    }

    "return OK when adding an XML notification" in {
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

      status(result) shouldBe Status.OK
    }

    "return CONFLICT when adding a duplicate notification" in {
      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      val fakePostRequest =
        FakeRequest("POST", "/notifications").withBody(Json.toJsObject(notification))

      val postResult = controller.receiveNotification()(fakePostRequest)

      status(postResult) shouldBe Status.OK

      val duplicateResult = controller.receiveNotification()(fakePostRequest)

      status(duplicateResult) shouldBe Status.CONFLICT
    }
  }
}
