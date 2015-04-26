/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers.unit

import controllers.Statistics
import helpers.EventHelper
import models.{ License, Event }
import org.joda.money.Money
import org.joda.time.LocalDate
import org.specs2.mutable.Specification
import stubs.FakeServices

class StatisticsSpec extends Specification {

  class TestStatistics extends Statistics with FakeServices {
    def callQuarterStatsByFacilitators(licenses: List[License]): List[(LocalDate, Int)] =
      quarterStatsByFacilitators(licenses)

    def callQuarterStatsByEvents(events: List[Event]): List[(LocalDate, Int)] =
      quarterStatsByEvents(events)
  }
  val controller = new TestStatistics

  "Results from method, calculating number of facilitators per quarter," should {
    val defaultLicense = License(None, 1L, 1L, "1", LocalDate.parse("2011-01-01"),
      LocalDate.parse("2011-01-01"), LocalDate.now().plusYears(1),
      confirmed = true, Money.parse("EUR 100"), Some(Money.parse("EUR 100")))

    val licenses = List(
      defaultLicense,
      defaultLicense.copy(start = LocalDate.parse("2011-02-12")),
      defaultLicense.copy(start = LocalDate.parse("2011-02-13")),
      defaultLicense.copy(start = LocalDate.parse("2011-08-15")),
      defaultLicense.copy(start = LocalDate.parse("2012-04-12")),
      defaultLicense.copy(start = LocalDate.parse("2013-05-11")))

    "include first month and current month even if they're in the middle of quarter" in {
      val res = controller.callQuarterStatsByFacilitators(licenses)
      res.head._1 must_== LocalDate.parse("2011-01-01")
      res.last._1 must_== LocalDate.now().withDayOfMonth(1)
    }
    "include accumulated statistics per number of facilitators per quarter" in {
      val res = controller.callQuarterStatsByFacilitators(licenses)
      res(1)._2 must_== 3 // first quarter
      res(3)._2 must_== 4 // third quarter
      res(6)._2 must_== 5 // sixth quarter
    }
    "include accumulated statistics taking expired licenses into account" in {
      val withExpired = defaultLicense.copy(end = LocalDate.parse("2012-01-11")) :: licenses
      val res = controller.callQuarterStatsByFacilitators(withExpired)
      res(1)._2 must_== 4 // first quarter
      res(3)._2 must_== 5 // third quarter
      res(5)._2 must_== 4 // sixth quarter
    }
    "work without issues when there's only one facilitator" in {
      val withExpired = defaultLicense.copy(end = LocalDate.parse("2012-01-11"))
      val res = controller.callQuarterStatsByFacilitators(List(withExpired))
      res(1)._2 must_== 1 // first quarter
      res(3)._2 must_== 1 // third quarter
      res(5)._2 must_== 0 // sixth quarter
    }
  }

  "Results from method, calculating accumulated number of event per quarter," should {
    val schedule = EventHelper.one.schedule
    val defaultEvent = EventHelper.one
    val events = List(
      defaultEvent.copy(schedule = schedule.copy(start = LocalDate.parse("2011-01-01"))),
      defaultEvent.copy(schedule = schedule.copy(start = LocalDate.parse("2011-02-12"))),
      defaultEvent.copy(schedule = schedule.copy(start = LocalDate.parse("2011-02-13"))),
      defaultEvent.copy(schedule = schedule.copy(start = LocalDate.parse("2011-08-15"))))

    "include first and last months even if they are in the middle of quarter" in {
      val res = controller.callQuarterStatsByEvents(events)
      res.head._1 must_== LocalDate.parse("2011-01-01")
      res.last._1 must_== LocalDate.now().withDayOfMonth(1)
    }
    "include accumulated statistics per number of events per quarter" in {
      val res = controller.callQuarterStatsByEvents(events)
      res(1)._2 must_== 3 // first quarter
      res(3)._2 must_== 4 // third quarter
      res(6)._2 must_== 4 // sixth quarter
    }
  }
}
