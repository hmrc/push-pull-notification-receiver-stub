/*
 * Copyright 2022 HM Revenue & Customs
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

import models.Challenge
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.ChallengeResponse

class ChallengeControllerSpec extends AnyWordSpec with Matchers {

  val controller = new ChallengeController(stubControllerComponents())

  "ChallengeController" should {
    "answer get requests with a challenge response" in {
      val request  = FakeRequest("GET", "/notifications")
      val response = controller.answerChallenge(Challenge("foo"))(request)
      status(response) shouldBe OK
      contentAsJson(response).as[ChallengeResponse] shouldBe ChallengeResponse(Challenge("foo"))
    }
  }
}
