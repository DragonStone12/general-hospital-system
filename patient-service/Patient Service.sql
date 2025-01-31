CREATE TYPE "visit_type" AS ENUM (
  'new_patient',
  'follow_up',
  'procedure'
);

CREATE TYPE "visit_status" AS ENUM (
  'scheduled',
  'completed',
  'cancelled',
  'no_show'
);

CREATE TYPE "document_status" AS ENUM (
  'pending',
  'completed',
  'expired'
);

CREATE TYPE "address_type" AS ENUM (
  'home',
  'work',
  'billing'
);

CREATE TYPE "insurance_plan_type" AS ENUM (
  'HMO',
  'PPO',
  'EPO',
  'POS',
  'HDHP',
  'HSA',
  'Medicare',
  'Medicaid',
  'Commercial',
  'Workers_Comp',
  'Auto_Insurance',
  'Medicare_Advantage',
  'Medicare_Supplement'
);

CREATE TABLE "providers" (
  "id" bigint PRIMARY KEY,
  "npi" varchar(10) UNIQUE NOT NULL,
  "first_name" varchar(100) NOT NULL,
  "last_name" varchar(100) NOT NULL,
  "credentials" varchar(20) NOT NULL,
  "specialty_id" bigint NOT NULL,
  "biography" text NOT NULL,
  "email" varchar(255) NOT NULL,
  "phone" varchar(20) NOT NULL,
  "active" boolean NOT NULL DEFAULT true,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  "updated_at" timestamp NOT NULL
);

CREATE TABLE "facilities" (
  "id" bigint PRIMARY KEY,
  "name" varchar(255) NOT NULL,
  "facility_type" varchar(100) NOT NULL,
  "description" text NOT NULL,
  "accepts_walkins" boolean NOT NULL DEFAULT false,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  "updated_at" timestamp NOT NULL
);

CREATE TABLE "insurance_carriers" (
  "id" bigint PRIMARY KEY,
  "name" varchar(255) NOT NULL,
  "display_name" varchar(255) NOT NULL,
  "total_plans" int NOT NULL,
  "contact_phone" varchar(20) NOT NULL,
  "website_url" varchar(255),
  "active" boolean NOT NULL DEFAULT true
);

CREATE TABLE "services" (
  "id" bigint PRIMARY KEY,
  "name" varchar(255) NOT NULL,
  "description" text NOT NULL,
  "service_category" varchar(100) NOT NULL,
  "duration_minutes" int NOT NULL,
  "requires_preparation" boolean NOT NULL,
  "preparation_instructions" text,
  "is_active" boolean NOT NULL DEFAULT true,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  "updated_at" timestamp NOT NULL
);

CREATE TABLE "insurance_plans" (
  "id" bigint PRIMARY KEY,
  "carrier_id" bigint NOT NULL,
  "plan_name" varchar(255) NOT NULL,
  "plan_type" insurance_plan_type NOT NULL,
  "group_number" varchar(50),
  "coverage_details" text,
  "verification_phone" varchar(20),
  "active" boolean NOT NULL DEFAULT true
);

CREATE TABLE "patients" (
  "id" bigint PRIMARY KEY,
  "mrn" varchar(50) UNIQUE NOT NULL,
  "first_name" varchar(100) NOT NULL,
  "last_name" varchar(100) NOT NULL,
  "date_of_birth" date NOT NULL,
  "gender" varchar(20) NOT NULL,
  "email" varchar(255) NOT NULL,
  "phone" varchar(20) NOT NULL,
  "preferred_language" varchar(50) NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  "updated_at" timestamp NOT NULL
);

CREATE TABLE "patient_addresses" (
  "id" bigint PRIMARY KEY,
  "patient_id" bigint NOT NULL,
  "address_type" address_type NOT NULL,
  "address_line1" varchar(255) NOT NULL,
  "address_line2" varchar(255),
  "city" varchar(100) NOT NULL,
  "state" varchar(2) NOT NULL,
  "zip" varchar(10) NOT NULL,
  "is_primary" boolean NOT NULL DEFAULT false
);

CREATE TABLE "patient_insurance" (
  "id" bigint PRIMARY KEY,
  "patient_id" bigint NOT NULL,
  "insurance_plan_id" bigint NOT NULL,
  "member_id" varchar(100) NOT NULL,
  "group_number" varchar(100) NOT NULL,
  "is_primary" boolean NOT NULL DEFAULT false,
  "effective_date" date NOT NULL,
  "termination_date" date
);

CREATE TABLE "patient_visits" (
  "id" bigint PRIMARY KEY,
  "patient_id" bigint NOT NULL,
  "provider_id" bigint NOT NULL,
  "facility_id" bigint NOT NULL,
  "appointment_id" bigint NOT NULL,
  "visit_type" visit_type NOT NULL,
  "visit_date" timestamp NOT NULL,
  "status" visit_status NOT NULL,
  "chief_complaint" text NOT NULL,
  "has_review" boolean NOT NULL DEFAULT false
);

CREATE TABLE "patient_documents" (
  "id" bigint PRIMARY KEY,
  "patient_id" bigint NOT NULL,
  "document_type" varchar(100) NOT NULL,
  "file_path" varchar(255) NOT NULL,
  "uploaded_at" timestamp NOT NULL,
  "status" document_status NOT NULL,
  "language" varchar(10) NOT NULL
);

CREATE TABLE "appointments" (
  "id" bigint PRIMARY KEY,
  "patient_id" bigint NOT NULL,
  "provider_id" bigint NOT NULL,
  "facility_id" bigint NOT NULL,
  "service_id" bigint NOT NULL,
  "appointment_type" varchar(50) NOT NULL,
  "start_time" timestamp NOT NULL,
  "end_time" timestamp NOT NULL,
  "status" varchar(20) NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

ALTER TABLE "patient_addresses" ADD FOREIGN KEY ("patient_id") REFERENCES "patients" ("id");

ALTER TABLE "patient_insurance" ADD FOREIGN KEY ("patient_id") REFERENCES "patients" ("id");

ALTER TABLE "patient_insurance" ADD FOREIGN KEY ("insurance_plan_id") REFERENCES "insurance_plans" ("id");

ALTER TABLE "patient_visits" ADD FOREIGN KEY ("patient_id") REFERENCES "patients" ("id");

ALTER TABLE "patient_visits" ADD FOREIGN KEY ("provider_id") REFERENCES "providers" ("id");

ALTER TABLE "patient_visits" ADD FOREIGN KEY ("facility_id") REFERENCES "facilities" ("id");

ALTER TABLE "patient_visits" ADD FOREIGN KEY ("appointment_id") REFERENCES "appointments" ("id");

ALTER TABLE "patient_documents" ADD FOREIGN KEY ("patient_id") REFERENCES "patients" ("id");

ALTER TABLE "appointments" ADD FOREIGN KEY ("patient_id") REFERENCES "patients" ("id");

ALTER TABLE "appointments" ADD FOREIGN KEY ("provider_id") REFERENCES "providers" ("id");

ALTER TABLE "appointments" ADD FOREIGN KEY ("facility_id") REFERENCES "facilities" ("id");

ALTER TABLE "appointments" ADD FOREIGN KEY ("service_id") REFERENCES "services" ("id");

ALTER TABLE "insurance_plans" ADD FOREIGN KEY ("carrier_id") REFERENCES "insurance_carriers" ("id");
