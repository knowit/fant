const uuidv1 = require('uuid/v1');
const NFC = require('../models/NFC');

module.exports.create = async (event, context) => {
  console.log(event);
  const nfc = new NFC({ key: uuidv1() });
  return new Promise((resolve, reject) =>
    nfc.save((error, result) => {
      if (result && result !== null) {
        resolve(result);
      }
      reject(error);
    })
  );
};

module.exports.find = (event, context) => {
  console.log(event);
  const body = JSON.parse(event.body)
  const key = body.nfc.key;
  console.log(key);
  return new Promise((resolve, reject) =>
    NFC.findOne({ key })
      .exec((error, result) => {
        if (result && result !== null) {
          resolve(result);
        }
        reject(error);
      })
  );
};
