package com.datastax.workshop.scala

import java.util.UUID

import org.junit.jupiter.api._
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession

@RunWith(classOf[JUnitPlatform])
object Ex08_Query4b_Read_Journey_Details {

  private val LOGGER                         = LoggerFactory.getLogger("Exercise4")
  private var cqlSession: CqlSession         = _
  private var journeyRepo: JourneyRepository = _

  @BeforeAll def initConnection(): Unit = {
    cqlSession = createCqlSession(LOGGER)
    journeyRepo = new JourneyRepository(cqlSession)
  }

  @AfterAll def closeConnectionToCassandra(): Unit =
    closeCqlSession(cqlSession, LOGGER)
}

@RunWith(classOf[JUnitPlatform])
class Ex08_Query4b_Read_Journey_Details {

  import Ex08_Query4b_Read_Journey_Details._

  /*
   * select * from spacecraft_journey_catalog
   * WHERE journey_id=47b04070-c4fb-11ea-babd-17b91da87c10
   * AND spacecraft_name='DragonCrew,SpaceX';
   */
  @Test
  def read_a_journey(): Unit = {

    journeyRepo.find(UUID.fromString(Ex04_Query5b_TakeOff.JOURNEY_ID), Ex04_Query5b_TakeOff.SPACECRAFT) match {
      case None          =>
        LOGGER.info("Journey {} not found, check class 'Ex04_ReadParsePage' or DB", Ex04_Query5b_TakeOff.JOURNEY_ID)
        LOGGER.info("FAILURE")
      case Some(journey) =>
        LOGGER.info("Journey has been found")
        LOGGER.info("- Uid:\t\t {}", journey.id)
        LOGGER.info("- Spacecraft:\t {}", journey.spaceCraft)
        LOGGER.info("- Summary:\t {}", journey.summary)
        LOGGER.info("- Takeoff:\t {}", journey.start)
        LOGGER.info("- Landing:\t {}", journey.end)
        LOGGER.info("SUCCESS")
    }
  }
}
