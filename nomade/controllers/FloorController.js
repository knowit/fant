const Floor = require("../models/Floor");
const Room = require("../models/Room");

module.exports.create = async (event, context) => {
  console.log(event);
  const floor = new Floor(JSON.parse(event.body));
  return new Promise((resolve, reject) =>
    floor.save((err, result) => {
      if (result && result !== null) {
        resolve(result);
      }
      resolve(err);
    })
  );
};

module.exports.find = (event, context) => {
  console.log(event);
  const name = event["queryStringParameters"]["name"];
  return new Promise((resolve, reject) =>
    Floor.findOne({ name })
      .populate({
        path: "rooms",
        select: "_id name capacity reservations",
        populate: { path: "reservations", select: "_id from to reservedBy" }
      })
      .select("_id name rooms")
      .exec((err, result) => {
        if (result && result !== null) {
          resolve(result);
        }
        resolve(err);
      })
  );
};
