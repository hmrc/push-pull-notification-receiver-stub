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

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.concurrent.Future

import NotificationsService._

case class FakeNotificationsService(initialData: Map[NotificationId, Notification])
    extends NotificationsService {

  private val localServerAddress: ServerAddress =
    ServerAddress("localhost", 27017)

  private val duplicateKeyError: DuplicateId =
    DuplicateId(
      new MongoWriteException(
        new WriteError(MongoUtils.DuplicateKey.Code, "Duplicate key error", BsonDocument()),
        localServerAddress
      )
    )

  private val data: ConcurrentHashMap[NotificationId, Notification] =
    new ConcurrentHashMap(initialData.asJava)

  override def getNotifications(boxId: BoxId): Future[Seq[Notification]] =
    Future.successful(
      data.asScala.collect {
        case (_, notification) if notification.boxId == boxId =>
          notification
      }.toSeq
    )

  override def saveNotification(
    notification: Notification
  ): Future[Either[NotificationsService.Error, Unit]] = {
    if (data.putIfAbsent(notification.notificationId, notification) != null) {
      Future.successful(Either.left(duplicateKeyError))
    } else {
      Future.successful(Either.right(()))
    }
  }

  override def deleteNotifications(): Future[Unit] =
    Future.successful(data.clear())
}
