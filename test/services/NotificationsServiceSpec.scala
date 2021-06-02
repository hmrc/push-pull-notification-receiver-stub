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

package services

import models.BoxId
import org.mockito.scalatest.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repositories.NotificationsRepository

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.test.FutureAwaits
import play.api.test.DefaultAwaitTimeout
import models.Notification
import models.XMLNotification
import models.NotificationId
import models.NotificationStatus
import java.time.OffsetDateTime
import com.mongodb.MongoWriteException
import com.mongodb.WriteError
import uk.gov.hmrc.mongo.MongoUtils
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.ServerAddress

class NotificationsServiceSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with FutureAwaits
    with DefaultAwaitTimeout {
  val mockRepository = mock[NotificationsRepository]

  val service = new NotificationsServiceImpl(mockRepository)

  "NotificationsService" should {
    val notification: Notification = XMLNotification(
      NotificationId(UUID.randomUUID),
      BoxId(UUID.randomUUID),
      <test/>,
      NotificationStatus.Acknowledged,
      OffsetDateTime.now
    )

    "delegate getNotifications call to repository" in {
      val boxId = BoxId(UUID.randomUUID)
      when(mockRepository.find(boxId)) thenReturn Future.successful(Seq.empty)

      await(service.getNotifications(boxId)) shouldBe Seq.empty

      when(mockRepository.find(boxId)) thenReturn Future.successful(Seq(notification))

      await(service.getNotifications(boxId)) shouldBe Seq(notification)
    }

    "return unit value for successful insert" in {
      when(mockRepository.insert(notification)) thenReturn Future.successful(())

      await(service.saveNotification(notification)) shouldBe Right(())
    }

    "convert duplicate key error to NotificationsService.DuplicateId" in {
      val mongoError = new MongoWriteException(
        new WriteError(MongoUtils.DuplicateKey.Code, "Duplicate key error", BsonDocument()),
        ServerAddress("localhost", 27017)
      )

      when(mockRepository.insert(notification)) thenReturn Future.failed(mongoError)

      await(service.saveNotification(notification)) shouldBe Left(
        NotificationsService.DuplicateId(mongoError)
      )
    }

    "rethrow unexpected exceptions" in {
      when(mockRepository.insert(notification)) thenReturn Future.failed(
        new RuntimeException("kaboom")
      )

      intercept[RuntimeException] {
        await(service.saveNotification(notification))
      }
    }
  }
}
