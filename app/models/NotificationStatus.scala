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

import play.api.libs.json.Format
import play.api.libs.json.JsonValidationError
import play.api.libs.json.Reads
import play.api.libs.json.Writes

sealed abstract class NotificationStatus(val name: String) extends Product with Serializable

object NotificationStatus {
  case object Pending      extends NotificationStatus("PENDING")
  case object Acknowledged extends NotificationStatus("ACKNOWLEDGED")
  case object Failed       extends NotificationStatus("FAILED")

  val values = Set(Pending, Acknowledged, Failed)

  def withName(name: String): NotificationStatus =
    values.find(_.name.equalsIgnoreCase(name)).getOrElse(throw new NoSuchElementException)

  implicit val notificationStatusReads: Reads[NotificationStatus] = Reads
    .of[String]
    .filter(JsonValidationError("error.expected.validenumvalue"))(values.map(_.name).contains(_))
    .map(withName)

  implicit val notificationStatusFormat: Format[NotificationStatus] = Format(
    notificationStatusReads,
    Writes.of[String].contramap(_.name)
  )
}
