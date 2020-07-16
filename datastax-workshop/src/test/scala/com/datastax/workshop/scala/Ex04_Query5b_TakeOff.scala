package com.datastax.workshop.scala

import java.util.UUID

import org.junit.jupiter.api._
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession

@RunWith(classOf[JUnitPlatform])
object Ex04_Query5b_TakeOff {

  private val LOGGER                         = LoggerFactory.getLogger("Exercise3")
  private var cqlSession: CqlSession         = _
  private var journeyRepo: JourneyRepository = _

  @BeforeAll def initConnection(): Unit = {
    cqlSession = createCqlSession(LOGGER)
    journeyRepo = new JourneyRepository(cqlSession)
  }

  @AfterAll def closeConnectionToCassandra(): Unit =
    closeCqlSession(cqlSession, LOGGER)

  // ===> WE WILL USE THIS VALUES EVERYWHERE
  var SPACECRAFT = "Crew Dragon Endeavour,SpaceX"
  var JOURNEY_ID = "90163870-c5d6-11ea-b11f-c30e2b038000"
}

@RunWith(classOf[JUnitPlatform])
class Ex04_Query5b_TakeOff {

  import Ex04_Query5b_TakeOff._

  @Test
  def takeoff_the_spacecraft(): Unit = {
    LOGGER.info("9..8..7..6..5..4..3..2..1 Ignition")
    journeyRepo
      .takeoff(UUID.fromString(JOURNEY_ID), SPACECRAFT)
    LOGGER.info("Journey {} has now taken off", JOURNEY_ID)
    LOGGER.info("SUCCESS")
  }
}
