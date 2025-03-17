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

package controllers

import base.WiremockSuite
import models.{Challenge, ChallengeResponse}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class ChallengeSpec
    extends AnyWordSpec
    with Matchers
    with GuiceOneServerPerSuite
    with WiremockSuite
    with FutureAwaits
    with DefaultAwaitTimeout {

  override protected def portConfigKeys: Seq[String] = Seq.empty

  lazy val ws: WSClient = app.injector.instanceOf[WSClient]

  "GET /notifications" should {
    "return OK and challenge response" in {
      val response = await(
        ws
          .url(
            s"http://localhost:$port/push-pull-notification-receiver-stub/notifications?challenge=foo"
          )
          .get()
      )

      response.status shouldBe Status.OK
      response.json.as[ChallengeResponse] shouldBe ChallengeResponse(Challenge("foo"))
    }

    "return BAD_REQUEST when the challenge query parameter is missing" in {
      val response = await(
        ws
          .url(
            s"http://localhost:$port/push-pull-notification-receiver-stub/notifications"
          )
          .get()
      )

      response.status shouldBe Status.BAD_REQUEST
    }
  }
}
