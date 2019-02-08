const util = require('./util/util');
const constants = require('./constants');

const NFC = require('./models/NFC');

const NFCController = require('./controllers/NFCController');

module.exports.nfc = async (event, context) => {
  await util.connectToDatabase(constants.MONGODB_URI);

  switch (event.httpMethod) {
    case 'POST':
      try {
        let result = await NFCController.find(event, context);
        console.log(result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: 'NFC find:',
            nfc: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 401,
          body: {
            error: e
          }
        };
      }
    case 'GET':
      try {
        let result = await NFCController.create(event, context);
        console.log(result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: 'NFC create:',
            nfc: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 500,
          body: {
            error: e
          }
        };
      }
  }
};