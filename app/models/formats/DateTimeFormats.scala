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

import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

object DateTimeFormats extends Logging {
  val formatter = new DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral("T")
    .appendPattern("HH:mm:ss")
    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
    .appendOffset("+HHmm", "+0000")
    .toFormatter

  implicit val offsetDateTimeReads: Reads[OffsetDateTime] =
    Reads.offsetDateTimeReads(formatter)

  implicit val offsetDateTimeWrites: Writes[OffsetDateTime] =
    Writes.of[String].contramap(formatter.format)
}
