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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.OffsetDateTime
import java.time.ZoneOffset

class DateTimeFormatsSpec extends AnyFlatSpec with Matchers {

  val expectedMillis = "2023-02-01T18:18:31.123+0000"
  val expectedNanos  = "2023-02-01T18:18:31.123456789+0100"

  "format" should "get the correct time when specified in millis" in {
    OffsetDateTime.from(DateTimeFormats.formatter.parse(expectedMillis)) shouldBe OffsetDateTime.of(
      2023,
      2,
      1,
      18,
      18,
      31,
      123000000,
      ZoneOffset.ofHours(0)
    )
  }

  "corrector" should "convert an incorrect date format" in {
    OffsetDateTime.from(DateTimeFormats.formatter.parse(expectedNanos)) shouldBe OffsetDateTime.of(
      2023,
      2,
      1,
      18,
      18,
      31,
      123456789,
      ZoneOffset.ofHours(1)
    )
  }

}
