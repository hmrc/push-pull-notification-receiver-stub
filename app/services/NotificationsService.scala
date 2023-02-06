/*
 * Copyright 2023 HM Revenue & Customs
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
import com.google.inject.ImplementedBy
import com.mongodb.MongoServerException
import models.BoxId
import models.Notification
import play.api.Logging
import repositories.NotificationsRepository
import uk.gov.hmrc.mongo.MongoUtils.DuplicateKey

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.control.NonFatal

import NotificationsService._

@ImplementedBy(classOf[NotificationsServiceImpl])
trait NotificationsService {
  def getNotifications(boxId: BoxId): Future[Seq[Notification]]

  def deleteNotifications(): Future[Unit]

  def saveNotification(notification: Notification): Future[Either[Error, Unit]]
}

class NotificationsServiceImpl @Inject() (repo: NotificationsRepository)(implicit
  ec: ExecutionContext
) extends NotificationsService
    with Logging {

  override def getNotifications(boxId: BoxId): Future[Seq[Notification]] = repo.find(boxId)

  override def deleteNotifications(): Future[Unit] =
    repo.deleteAll().transformWith {
      case Success(_) =>
        Future.successful(())

      case Failure(NonFatal(e)) =>
        logger.error(
          s"Unexpected error while deleting notifications",
          e
        )

        Future.failed(e)
    }

  override def saveNotification(notification: Notification): Future[Either[Error, Unit]] =
    repo.insert(notification).transformWith {
      case Success(_) =>
        Future.successful(Either.right(()))

      case Failure(DuplicateKey(e)) =>
        logger.error(
          s"Duplicate key error while inserting notification ${notification.notificationId.value}",
          e
        )

        Future.successful(Either.left(DuplicateId(e)))

      case Failure(NonFatal(e)) =>
        logger.error(
          s"Unexpected error while inserting notification ${notification.notificationId.value}",
          e
        )

        Future.failed(e)

    }
}

object NotificationsService {
  sealed abstract class Error(val message: String)
      extends Throwable(message)
      with Product
      with Serializable

  case class DuplicateId(exception: MongoServerException) extends Error(exception.getMessage)
}
