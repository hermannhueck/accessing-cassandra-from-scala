package com.datastax.workshop

import com.datastax.oss.driver.api.core.CqlSession
import java.nio.file.Paths
import org.slf4j.Logger

package object scala {

  def sessionBuilder =
    CqlSession
      .builder
      .withCloudSecureConnectBundle(Paths.get(DBConnection.SECURE_CONNECT_BUNDLE))
      .withAuthCredentials(DBConnection.USERNAME, DBConnection.PASSWORD)
      .withKeyspace(DBConnection.KEYSPACE)

  def createCqlSession(logger: Logger): CqlSession = {
    logger.info("========================================")
    logger.info("----- Start exercise")
    sessionBuilder.build
  }

  def closeCqlSession(cqlSession: CqlSession, logger: Logger): Unit = {
    if (null != cqlSession)
      cqlSession.close
    logger.info("----- Stop exercise")
    logger.info("========================================")
  }
}
