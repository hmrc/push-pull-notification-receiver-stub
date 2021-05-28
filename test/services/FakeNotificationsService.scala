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

import cats.syntax.all._
import com.mongodb.MongoWriteException
import com.mongodb.WriteError
import models.BoxId
import models.Notification
import models.NotificationId
import org.mongodb.scala.ServerAddress
import org.mongodb.scala.bson.BsonDocument
import uk.gov.hmrc.mongo.MongoUtils

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import NotificationsService._

case class FakeNotificationsService(initialData: Map[NotificationId, Notification])(implicit
  ec: ExecutionContext
) extends NotificationsService {

  private val localServerAddress: ServerAddress =
    ServerAddress("localhost", 27017)

  private val duplicateKeyError: DuplicateId =
    DuplicateId(
      new MongoWriteException(
        new WriteError(MongoUtils.DuplicateKey.Code, "Duplicate key error", BsonDocument()),
        localServerAddress
      )
    )

  private val data: AtomicReference[Map[NotificationId, Notification]] =
    new AtomicReference(initialData)

  override def getNotifications(boxId: BoxId): Future[Seq[Notification]] =
    Future.successful(
      data
        .get()
        .collect {
          case (_, notification) if notification.boxId == boxId =>
            notification
        }
        .toSeq
    )

  override def saveNotification(
    notification: Notification
  ): Future[Either[NotificationsService.Error, Unit]] = {
    if (data.get().contains(notification.notificationId))
      Future.successful(Either.left(duplicateKeyError))
    else
      Future
        .successful(data.updateAndGet { currentData =>
          currentData + (notification.notificationId -> notification)
        })
        .map(_ => Either.right(()))
  }

  def clear(): Unit =
    data.set(Map.empty)
}
