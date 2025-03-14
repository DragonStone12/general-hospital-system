
-- Appointments table for the MySQL database --
CREATE TABLE appointments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    facility_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    appointment_type ENUM('video', 'in_person', 'phone') NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    visit_status ENUM('scheduled', 'completed', 'cancelled', 'no_show') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    PRIMARY KEY (id, start_time)
) PARTITION BY RANGE (UNIX_TIMESTAMP(start_time));

-- Create quarterly partitions for the current year
CREATE TABLE appointments_2025_q1 PARTITION OF appointments
    FOR VALUES FROM (UNIX_TIMESTAMP('2025-01-01')) TO (UNIX_TIMESTAMP('2025-04-01'));

CREATE TABLE appointments_2025_q2 PARTITION OF appointments
    FOR VALUES FROM (UNIX_TIMESTAMP('2025-04-01')) TO (UNIX_TIMESTAMP('2025-07-01'));

CREATE TABLE appointments_2025_q3 PARTITION OF appointments
    FOR VALUES FROM (UNIX_TIMESTAMP('2025-07-01')) TO (UNIX_TIMESTAMP('2025-10-01'));

CREATE TABLE appointments_2025_q4 PARTITION OF appointments
    FOR VALUES FROM (UNIX_TIMESTAMP('2025-10-01')) TO (UNIX_TIMESTAMP('2026-01-01'));

-- Create indexes on frequently queried columns in each partition
CREATE INDEX idx_appointments_2025_q1_provider_id ON appointments_2025_q1 (provider_id);
CREATE INDEX idx_appointments_2025_q2_provider_id ON appointments_2025_q2 (provider_id);
CREATE INDEX idx_appointments_2025_q3_provider_id ON appointments_2025_q3 (provider_id);
CREATE INDEX idx_appointments_2025_q4_provider_id ON appointments_2025_q4 (provider_id);
