
-- Create the partitioned provider_availability table
CREATE TABLE provider_availability (
  id BIGSERIAL NOT NULL,
  provider_id BIGINT NOT NULL,
  facility_id BIGINT NOT NULL,
  date date NOT NULL,
  start_time time NOT NULL,
  end_time time NOT NULL,
  is_available boolean NOT NULL DEFAULT true,
  PRIMARY KEY (id, date),
  CONSTRAINT fk_provider_availability_provider
      FOREIGN KEY (provider_id)
          REFERENCES providers (id)
) PARTITION BY RANGE (date);

-- Create quarterly partitions for 2025
CREATE TABLE provider_availability_2025_q1 PARTITION OF provider_availability
  FOR VALUES FROM ('2025-01-01') TO ('2025-04-01');

CREATE TABLE provider_availability_2025_q2 PARTITION OF provider_availability
  FOR VALUES FROM ('2025-04-01') TO ('2025-07-01');

CREATE TABLE provider_availability_2025_q3 PARTITION OF provider_availability
  FOR VALUES FROM ('2025-07-01') TO ('2025-10-01');

CREATE TABLE provider_availability_2025_q4 PARTITION OF provider_availability
  FOR VALUES FROM ('2025-10-01') TO ('2026-01-01');

-- Create indexes on provider_id for each partition
CREATE INDEX idx_provider_avail_2025_q1_provider ON provider_availability_2025_q1 (provider_id);
CREATE INDEX idx_provider_avail_2025_q2_provider ON provider_availability_2025_q2 (provider_id);
CREATE INDEX idx_provider_avail_2025_q3_provider ON provider_availability_2025_q3 (provider_id);
CREATE INDEX idx_provider_avail_2025_q4_provider ON provider_availability_2025_q4 (provider_id);
