const uuidv1 = require('uuid/v1');
const NFC = require('../models/NFC');

module.exports.create = async (event, context) => {
  console.log(event);
  const nfc = new NFC(uuidv1());
  return new Promise((resolve, reject) =>
    nfc.save((error, result) => {
      if (result && result !== null) {
        resolve(result);
      }
      resolve(error);
    })
  );
};

module.exports.find = (event, context) => {
  console.log(event);
  const key = event["queryStringParameters"]["key"];
  return new Promise((resolve, reject) =>
    NFC.findOne({ key })
      .populate({
        path: "rooms",
        select: "_id name capacity reservations",
        populate: { path: "reservations", select: "_id from to reservedBy" }
      })
      .select("_id name rooms")
      .exec((error, result) => {
        if (result && result !== null) {
          resolve(result);
        }
        resolve(error);
      })
  );
};
