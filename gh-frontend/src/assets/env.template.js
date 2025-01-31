// This file serves as a template that will be processed at container startup
(function(window) {
  window["env"] = window["env"] || {};
  // Environment variables
  window["env"]["patientServiceUrl"] = "${PATIENT_SERVICE_URL}";
  window["env"]["appointmentServiceUrl"] = "${APPOINTMENT_SERVICE_URL}";
  window["env"]["providerServiceUrl"] = "${PROVIDER_SERVICE_URL}";
  window["env"]["dispatcherServiceUrl"] = "${DISPATCHER_SERVICE_URL}";
})(this);
