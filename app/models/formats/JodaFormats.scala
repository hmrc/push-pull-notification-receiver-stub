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

package models.formats

import play.api.Logging
import play.api.libs.json.Reads

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import play.api.libs.json.Writes

object JodaFormats extends Logging {
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")

  implicit val offsetDateTimeReads: Reads[OffsetDateTime] =
    Reads.offsetDateTimeReads(formatter, corrector)

  implicit val offsetDateTimeWrites: Writes[OffsetDateTime] =
    Writes.of[String].contramap(formatter.format)

  // TODO: Temporary fix to support invalid formats from push-pull-notification-api 0.73.0
  def corrector(input: String): String =
    if (input.endsWith("Z")) {
      logger.warn(s"Incorrect date-time format has been provided ($input) -- correcting.")
      formatter.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(input))
    }
    else input
}
