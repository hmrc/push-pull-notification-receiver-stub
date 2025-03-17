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

package models
package formats

import play.api.http.MimeTypes
import play.api.libs.json.{Format, JsObject, JsPath, Json, OFormat, OWrites, Reads, Writes, __}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import JsonFormats.*
import XMLFormats.*

import java.time.{Instant, LocalDateTime, OffsetDateTime, ZoneOffset}

object MongoFormats extends MongoJavatimeFormats {

  final val localDateTimeReads: Reads[LocalDateTime] =
    Reads
      .at[String](__ \ "$date" \ "$numberLong")
      .map(dateTime => Instant.ofEpochMilli(dateTime.toLong).atZone(ZoneOffset.UTC).toLocalDateTime)

  implicit val offsetDateTimeReads: Reads[OffsetDateTime] = Reads { value =>
    localDateTimeReads
      .reads(value)
      .map(localDateTime => localDateTime.atOffset(ZoneOffset.UTC))
  }

  final val localDateTimeWrites: Writes[LocalDateTime] =
    Writes
      .at[String](__ \ "$date" \ "$numberLong")
      .contramap(_.toInstant(ZoneOffset.UTC).toEpochMilli.toString)

  implicit val offsetDateTimeWrites: Writes[OffsetDateTime] = Writes { value =>
    localDateTimeWrites.writes(value.toLocalDateTime)
  }

  implicit val offsetDateTimeFormat: Format[OffsetDateTime] =
    Format.apply(offsetDateTimeReads, offsetDateTimeWrites)

  implicit val jsonNotificationReads: Reads[JsonNotification] =
    Json.reads[JsonNotification]
  implicit val jsonNotificationWrites: OWrites[JsonNotification] =
    Json.writes[JsonNotification].transform(_ ++ Json.obj("messageContentType" -> MimeTypes.JSON))
  implicit val jsonNotificationFormat: OFormat[JsonNotification] =
    OFormat(jsonNotificationReads, jsonNotificationWrites)

  implicit val xmlNotificationReads: Reads[XMLNotification] =
    Json.reads[XMLNotification]
  implicit val xmlNotificationWrites: OWrites[XMLNotification] =
    Json.writes[XMLNotification].transform(_ ++ Json.obj("messageContentType" -> MimeTypes.XML))
  implicit val xmlNotificationFormat: OFormat[XMLNotification] =
    OFormat(xmlNotificationReads, xmlNotificationWrites)

  implicit val notificationReads: Reads[Notification] =
    (JsPath \ "messageContentType").read[String].flatMap {
      case MimeTypes.JSON =>
        jsonNotificationFormat.widen[Notification]
      case MimeTypes.XML =>
        xmlNotificationFormat.widen[Notification]
    }

  implicit val notificationWrites: OWrites[Notification] = new OWrites[Notification] {
    override def writes(notification: Notification): JsObject = notification match {
      case json @ JsonNotification(_, _, _, _, _) =>
        jsonNotificationFormat.writes(json) ++ Json.obj(
          "messageContentType" -> MimeTypes.JSON
        )
      case xml @ XMLNotification(_, _, _, _, _) =>
        xmlNotificationFormat.writes(xml) ++ Json.obj(
          "messageContentType" -> MimeTypes.XML
        )
    }
  }

  implicit val notificationFormat: OFormat[Notification] =
    OFormat(notificationReads, notificationWrites)
}
