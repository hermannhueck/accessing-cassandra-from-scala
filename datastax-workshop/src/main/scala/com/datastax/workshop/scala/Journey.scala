package com.datastax.workshop.scala

import java.time.Instant
import java.util.UUID
import com.datastax.oss.driver.api.core.cql.Row

case class Journey(
    id: UUID,
    summary: String,
    start: Instant,
    end: Instant,
    spaceCraft: String,
    active: Boolean
)

case object Journey {

  import DataModelConstants._

  def apply(row: Row): Journey =
    Journey(
      row.getUuid(JOURNEY_ID),
      row.getString(JOURNEY_SUMMARY),
      row.getInstant(JOURNEY_START),
      row.getInstant(JOURNEY_END),
      row.getString(JOURNEY_SPACECRAFT_NAME),
      row.getBoolean(JOURNEY_ACTIVE)
    )
}
