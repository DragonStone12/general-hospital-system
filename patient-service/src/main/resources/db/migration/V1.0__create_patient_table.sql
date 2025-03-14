CREATE TYPE language_code AS ENUM ('en_US', 'es_ES', 'fr_FR', 'zh_CN', 'ja_JP', 'ko_KR', 'de_DE', 'it_IT', 'ru_RU');
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE');
CREATE TYPE address_type AS ENUM ('HOME', 'WORK', 'MAIL');
CREATE TYPE visit_type AS ENUM ('NEW_PATIENT', 'FOLLOW_UP', 'PROCEDURE');
CREATE TYPE visit_status AS ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW');


CREATE TABLE patients_details (
  id BIGSERIAL PRIMARY KEY,
  mrn VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  version int DEFAULT 0 NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  middle_name VARCHAR(100),
  last_name VARCHAR(100) NOT NULL,
  preferred_name VARCHAR(100),
  phone VARCHAR(20) NOT NULL,
  is_active BOOLEAN DEFAULT true NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  created_by VARCHAR(50) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  updated_by VARCHAR(50) NOT NULL,
);


CREATE TABLE patient_visits (
   id BIGSERIAL PRIMARY KEY,
   patient_details_id BIGINT NOT NULL,
   provider_id BIGINT NOT NULL,
   facility_id BIGINT NOT NULL,
   appointment_id BIGINT NOT NULL,
   version int DEFAULT 0 NOT NULL,
   visit_type visit_type NOT NULL,
   visit_date TIMESTAMP NOT NULL,
   visit_status visit_status NOT NULL,
   chief_complaint TEXT NOT NULL,
   has_review BOOLEAN DEFAULT false NOT NULL,
   CONSTRAINT fk_patient_patient_visits
      FOREIGN KEY (patient_details_id)
        REFERENCES patients_details(id)
           ON DELETE CASCADE
);


CREATE TABLE patient_addresses (
   id BIGSERIAL PRIMARY KEY,
   patient_details_id BIGINT NOT NULL,
   version int DEFAULT 0 NOT NULL,
   address_type address_type NOT NULL,
   address_line1 VARCHAR(255) NOT NULL,
   address_line2 VARCHAR(255),
   city VARCHAR(100) NOT NULL,
   state VARCHAR(50) NOT NULL,
   zipcode VARCHAR(20) NOT NULL,
   is_primary BOOLEAN DEFAULT false NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
   created_by VARCHAR(50) NOT NULL,
   updated_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
   updated_by VARCHAR(50) NOT NULL,
   CONSTRAINT fk_patient_address_patient
     FOREIGN KEY (patient_details_id)
       REFERENCES patients_details(id)
          ON DELETE CASCADE
);


CREATE TABLE patients_phi (
  id BIGSERIAL PRIMARY KEY,
  version int DEFAULT 0 NOT NULL,
  date_of_birth DATE NOT NULL,
  gender gender_type NOT NULL,
  preferred_language language_code NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  created_by VARCHAR(50) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  updated_by VARCHAR(50) NOT NULL,
  CONSTRAINT fk_patients_phi_basic
    FOREIGN KEY (id)
     REFERENCES patients_details(id)
       ON DELETE CASCADE
);

-- Create view for complete patient data
CREATE VIEW patients_complete AS
SELECT
  b.id, b.mrn, b.version, b.email, b.first_name, b.middle_name, b.last_name,
  b.preferred_name, b.phone, b.is_active, c.date_of_birth, c.gender,
  c.preferred_language, b.created_at, b.created_by, b.updated_at, b.updated_by
FROM patients_details b
JOIN patients_phi c ON b.id = c.id;



CREATE TABLE patient_visits_partitioned (
   id BIGSERIAL NOT NULL,
   version int DEFAULT 0 NOT NULL,
   patient_details_id BIGINT NOT NULL,
   provider_id BIGINT NOT NULL,
   facility_id BIGINT NOT NULL,
   appointment_id BIGINT NOT NULL,
   visit_type visit_type NOT NULL,
   visit_date TIMESTAMP NOT NULL,
   visit_status visit_status NOT NULL,
   chief_complaint TEXT NOT NULL,
   has_review BOOLEAN DEFAULT false NOT NULL,
   PRIMARY KEY (id, visit_date),
   CONSTRAINT fk_patient_visits_patient_details_id
      FOREIGN KEY (patient_details_id) REFERENCES patients_details(id)
      ON DELETE CASCADE
) PARTITION BY RANGE (visit_date);

-- Create quarterly partitions for the current year
CREATE TABLE patient_visits_2025_q1 PARTITION OF patient_visits_partitioned
  FOR VALUES FROM ('2025-01-01') TO ('2025-04-01');

CREATE TABLE patient_visits_2025_q2 PARTITION OF patient_visits_partitioned
  FOR VALUES FROM ('2025-04-01') TO ('2025-07-01');

CREATE TABLE patient_visits_2025_q3 PARTITION OF patient_visits_partitioned
  FOR VALUES FROM ('2025-07-01') TO ('2025-10-01');

CREATE TABLE patient_visits_2025_q4 PARTITION OF patient_visits_partitioned
  FOR VALUES FROM ('2025-10-01') TO ('2026-01-01');

-- Create indexes for efficient querying
CREATE INDEX idx_patients_details_last_name ON patients_details (last_name, first_name);
CREATE INDEX idx_patients_phi_dob ON patients_phi (date_of_birth);

-- Create indexes on partitions
CREATE INDEX idx_patient_visits_2025_q1_provider_id ON patient_visits_2025_q1 (provider_id);
CREATE INDEX idx_patient_visits_2025_q1_patient_details_id ON patient_visits_2025_q1 (patient_details_id);
CREATE INDEX idx_patient_visits_2025_q1_visit_status ON patient_visits_2025_q1 (visit_status);

CREATE INDEX idx_patient_visits_2025_q2_provider_id ON patient_visits_2025_q2 (provider_id);
CREATE INDEX idx_patient_visits_2025_q2_patient_details_id ON patient_visits_2025_q2 (patient_details_id);
CREATE INDEX idx_patient_visits_2025_q2_visit_status ON patient_visits_2025_q2 (visit_status);

CREATE INDEX idx_patient_visits_2025_q3_provider_id ON patient_visits_2025_q3 (provider_id);
CREATE INDEX idx_patient_visits_2025_q3_patient_details_id ON patient_visits_2025_q3 (patient_details_id);
CREATE INDEX idx_patient_visits_2025_q3_visit_status ON patient_visits_2025_q3 (visit_status);

CREATE INDEX idx_patient_visits_2025_q4_provider_id ON patient_visits_2025_q4 (provider_id);
CREATE INDEX idx_patient_visits_2025_q4_patient_details_id ON patient_visits_2025_q4 (patient_details_id);
CREATE INDEX idx_patient_visits_2025_q4_visit_status ON patient_visits_2025_q4 (visit_status);
