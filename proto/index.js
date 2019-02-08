var awsIot = require('aws-iot-device-sdk');

//
// Replace the values of '<YourUniqueClientIdentifier>' and '<YourCustomEndpoint>'
// with a unique client identifier and custom host endpoint provided in AWS IoT.
// NOTE: client identifiers must be unique within your AWS account; if a client attempts 
// to connect with a client identifier which is already in use, the existing 
// connection will be terminated.
//

const CERT_ROOT_PATH = '/Users/iver/tmp/iot';

var device = awsIot.device({
   keyPath: `${CERT_ROOT_PATH}/130d0919ec-private.pem.key`,
  certPath: `${CERT_ROOT_PATH}/130d0919ec-certificate.pem.crt`,
    caPath: `${CERT_ROOT_PATH}/AmazonRootCA1.pem`, // <YourRootCACertificatePath>,
  clientId: 'iver', // <YourUniqueClientIdentifier>,
      host: 'a1wn77w8brnymu-ats.iot.eu-west-2.amazonaws.com',
});

//
// Device is an instance returned by mqtt.Client(), see mqtt.js for full
// documentation.
//
device
  .on('connect', function() {
    console.log('connect');
    device.subscribe('temperature');
    device.publish('temperature', JSON.stringify({ temperature: 13.37 }));
  });

device
  .on('message', function(topic, payload) {
    console.log('message', topic, payload.toString());
  });