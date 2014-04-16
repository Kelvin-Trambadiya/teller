/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import models.database.{ Evaluations }
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import play.api.libs.Crypto
import scala.util.Random

/**
 * A status of an evaluation which a participant gives to an event
 */
object EvaluationStatus extends Enumeration {
  val Pending = Value("1")
  val Approved = Value("2")
  val Rejected = Value("3")
}

/**
 * An evaluation which a participant gives to an event
 */
case class Evaluation(
  id: Option[Long],
  eventId: Long,
  participantId: Option[Long],
  question1: String,
  question2: String,
  question3: String,
  question4: String,
  question5: String,
  question6: Int,
  question7: Int,
  question8: String,
  status: EvaluationStatus.Value,
  handled: Option[LocalDate],
  certificate: Option[String],
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val event: Event = Event.find(eventId).get

  lazy val participant: Person = Person.find(participantId.get).get

  def contributors: List[ContributorView] = Contribution.contributors(this.id.get)

  def insert: Evaluation = DB.withSession { implicit session: Session ⇒
    val id = Evaluations.forInsert.insert(this)
    this.copy(id = Some(id))
  }

  def delete(): Unit = Evaluation.delete(this.id.get)

  def update: Evaluation = DB.withSession { implicit session: Session ⇒
    val updateTuple = (eventId, participantId, question1, question2, question3, question4, question5,
      question6, question7, question8, status, handled, certificate, updated, updatedBy)
    val updateQuery = Evaluations.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }
}

object Evaluation {

  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.id === id).firstOption
  }

  def findAll: List[Evaluation] = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).sortBy(_.created).list
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Evaluations.where(_.id === id).mutate(_.delete())
  }

}
