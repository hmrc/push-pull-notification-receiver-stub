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

package uk.gov.hmrc.pushpullnotificationreceiverstub.models

import play.api.http.MimeTypes
import play.api.libs.functional.syntax._
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsPath
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.ws.XML

import java.time.OffsetDateTime
import scala.xml.Elem
import scala.xml.NodeSeq

import XMLFormats._

trait Notification extends Product with Serializable {
  def notificationId: NotificationId
  def boxId: BoxId
  def status: NotificationStatus
  def createdDateTime: OffsetDateTime
}

case class JsonNotification(
  notificationId: NotificationId,
  boxId: BoxId,
  message: JsValue,
  status: NotificationStatus,
  createdDateTime: OffsetDateTime
) extends Notification

object JsonNotification {
  implicit val jsonNotificationFormat: OFormat[JsonNotification] = Json.format[JsonNotification]
}

case class XMLNotification(
  notificationId: NotificationId,
  boxId: BoxId,
  message: Elem,
  status: NotificationStatus,
  createdDateTime: OffsetDateTime
) extends Notification

object XMLNotification {
  implicit val xmlNotificationFormat: OFormat[XMLNotification] = Json.format[XMLNotification]
}

object Notification {
  implicit val notificationReads: Reads[Notification] =
    (JsPath \ "messageContentType").read[String].flatMap {
      case MimeTypes.JSON =>
        JsonNotification.jsonNotificationFormat.widen[Notification]
      case MimeTypes.XML =>
        XMLNotification.xmlNotificationFormat.widen[Notification]
    }

  implicit val notificationWrites: OWrites[Notification] = new OWrites[Notification] {
    override def writes(notification: Notification): JsObject = notification match {
      case json @ JsonNotification(_, _, _, _, _) =>
        JsonNotification.jsonNotificationFormat.writes(json) ++ Json.obj(
          "messageContentType" -> MimeTypes.JSON
        )
      case xml @ XMLNotification(_, _, _, _, _) =>
        XMLNotification.xmlNotificationFormat.writes(xml) ++ Json.obj(
          "messageContentType" -> MimeTypes.XML
        )
    }
  }

  implicit val notificationFormat: OFormat[Notification] =
    OFormat(notificationReads, notificationWrites)
}
