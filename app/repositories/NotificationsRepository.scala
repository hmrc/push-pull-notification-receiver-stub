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

package repositories

import models.MongoFormats
import models.Notification
import org.mongodb.scala.model.IndexModel
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Indexes
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import models.BoxId
import org.mongodb.scala.model.Filters
import scala.concurrent.Future
import com.mongodb.client.result.InsertOneResult

@Singleton
class NotificationsRepository @Inject() (mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[Notification](
      mongoComponent = mongo,
      collectionName = "notifications",
      domainFormat = MongoFormats.notificationFormat,
      indexes = Seq(
        IndexModel(Indexes.ascending("notificationId"), IndexOptions().unique(true)),
        IndexModel(Indexes.ascending("boxId"))
      )
    ) {

  def find(boxId: BoxId): Future[Seq[Notification]] =
    collection.find(Filters.eq("boxId", boxId.value.toString)).toFuture

  def insert(notification: Notification): Future[Unit] =
    collection.insertOne(notification).toFuture.map(_ => ())
}
