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
import models.Notification
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import services.NotificationsService
import services.NotificationsService.DuplicateId
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.control.NonFatal

class NotificationsController @Inject() (
  notificationsService: NotificationsService,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def getNotifications(boxId: BoxId): Action[AnyContent] = Action.async { _ =>
    notificationsService.getNotifications(boxId).map(results => Ok(Json.toJson(results)))
  }

  def receiveNotification: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[Notification] { notification =>
      notificationsService.saveNotification(notification).transformWith {
        case Success(Right(_))             => Future.successful(Ok)
        case Success(Left(DuplicateId(_))) => Future.successful(Conflict)
        case Failure(NonFatal(_))          => Future.successful(InternalServerError)
      }
    }
  }
}
