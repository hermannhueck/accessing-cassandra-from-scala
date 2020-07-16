package com.datastax.workshop.scala

import java.util.UUID

import org.junit.jupiter.api._

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession

@RunWith(classOf[JUnitPlatform])
object Ex06_Query5d_Landing {

  private val LOGGER                         = LoggerFactory.getLogger("Exercise3")
  private var cqlSession: CqlSession         = _
  private var journeyRepo: JourneyRepository = _

  @BeforeAll def initConnection(): Unit = {
    cqlSession = createCqlSession(LOGGER)
    journeyRepo = new JourneyRepository(cqlSession)
  }

  @AfterAll def closeConnectionToCassandra(): Unit =
    closeCqlSession(cqlSession, LOGGER)
}

@RunWith(classOf[JUnitPlatform]) class Ex06_Query5d_Landing {

  import Ex06_Query5d_Landing._

  @Test
  def landing_journey(): Unit = {
    journeyRepo
      .landing(UUID.fromString(Ex04_Query5b_TakeOff.JOURNEY_ID), Ex04_Query5b_TakeOff.SPACECRAFT)
    LOGGER.info("Journey {} has now landed", Ex04_Query5b_TakeOff.JOURNEY_ID)
    LOGGER.info("SUCCESS")
  }
}
