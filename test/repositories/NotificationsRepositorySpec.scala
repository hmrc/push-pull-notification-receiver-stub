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

package repositories

import cats.syntax.all._
import com.mongodb.MongoWriteException
import models.BoxId
import models.JsonNotification
import models.Notification
import models.NotificationId
import models.NotificationStatus
import models.XMLNotification
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import play.api.test.DefaultAwaitTimeout
import play.api.test.FutureAwaits
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.OffsetDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class NotificationsRepositorySpec
    extends AnyWordSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[Notification]
    with DefaultAwaitTimeout
    with FutureAwaits {

  override protected lazy val repository: NotificationsRepository =
    new NotificationsRepository(mongoComponent)

  "NotificationsRepository.find" should {
    "return no results when first initialised" in {
      await(repository.collection.find().toFuture) shouldBe Seq.empty
    }

    "successfully insert notifications" in {
      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      await(repository.find(notification.boxId)) shouldBe Seq.empty

      await(repository.insert(notification)) shouldBe (())

      await(repository.find(notification.boxId)) shouldBe Seq(notification)
    }

    "only return notifications for the given box ID" in {
      val boxId1 = BoxId(UUID.randomUUID)
      val boxId2 = BoxId(UUID.randomUUID)

      val notification1: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        boxId1,
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )
      val notification2: Notification = XMLNotification(
        NotificationId(UUID.randomUUID),
        boxId1,
        <test/>,
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )
      val notification3: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        boxId2,
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      await(List(notification1, notification2, notification3).traverse(repository.insert))
      await(repository.find(boxId1)).toSet shouldBe Set(notification1, notification2)
      await(repository.find(boxId2)).toSet shouldBe Set(notification3)
    }

    "prevent inserting duplicate notifications" in {
      val notification: Notification = JsonNotification(
        NotificationId(UUID.randomUUID),
        BoxId(UUID.randomUUID),
        Json.toJson(Json.obj()),
        NotificationStatus.Acknowledged,
        OffsetDateTime.now
      )

      await(repository.insert(notification)) shouldBe (())

      intercept[MongoWriteException] {
        await(repository.insert(notification))
      }
    }
  }
}
