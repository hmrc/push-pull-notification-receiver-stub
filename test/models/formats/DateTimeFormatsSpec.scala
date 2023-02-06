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

class DateTimeFormatsSpec extends AnyFlatSpec with Matchers {

  val expected = "2023-02-01T18:18:31.000+0000"

  "corrector" should "not convert a correct date format" in {
    DateTimeFormats.corrector(expected) shouldBe expected
  }

  "corrector" should "convert an incorrect date format" in {
    val input = "2023-02-01T18:18:31.000Z"
    DateTimeFormats.corrector(input) shouldBe expected
  }

}
