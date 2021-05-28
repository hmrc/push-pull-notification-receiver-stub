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
import models.Notification
import repositories.NotificationsRepository

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import org.mongodb.scala.model.Filters

class NotificationsService @Inject() (repo: NotificationsRepository)(implicit ec: ExecutionContext) {
  def getNotifications(boxId: BoxId): Future[Seq[Notification]] = repo.find(boxId)
  def saveNotification(notification: Notification): Future[Unit] = repo.insert(notification)
}
