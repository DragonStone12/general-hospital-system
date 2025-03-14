export const environment = {
  production: false,
  patientServiceUrl: process.env['PATIENT_SERVICE_URL'] || 'http://localhost:9010',
  appointmentServiceUrl: process.env['APPOINTMENT_SERVICE_URL'] || 'http://localhost:9020',
  providerServiceUrl: process.env['PROVIDER_SERVICE_URL'] || 'http://localhost:9040',
  dispatcherServiceUrl: process.env['DISPATCHER_SERVICE_URL'] || 'http://localhost:9030'
};
