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

import javax.inject.Inject
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import models.Notification
import play.api.libs.json.Json
import scala.concurrent.Future
import play.api.libs.json.JsValue
import models.JsonNotification

class NotificationsController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  def getNotifications: Action[AnyContent] = Action { _ =>
    Ok(Json.toJson(Seq.empty[Notification]))
  }

  def receiveNotification: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[Notification] { request =>
      Future.successful(Accepted)
    }
  }
}
