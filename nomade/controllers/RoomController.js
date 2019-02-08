const Floor = require("../models/Floor");
const Room = require("../models/Room");

module.exports.findAll = async (event, context) => {
  console.log(event);
  return new Promise((resolve, reject) =>
    Room.find({})
      .populate("floor")
      .exec((err, result) => {
        if (result && result !== null) {
          resolve(result);
        }
        resolve(err);
      })
  );
};

module.exports.create = async (event, context) => {
  console.log(event);
  const body = JSON.parse(event.body);
  return new Promise((resolve, reject) =>
    Floor.findOne({ name: body.floor }, (err, floor) => {
      console.log("Error findOne", err);
      if (err) {
        resolve(err);
      }
      console.log("Result findOne", floor);
      const room = new Room({ ...body, floor: floor._id });

      room.save((err, result) => {
        console.log("Error save room", err);
        if (err) {
          resolve(err);
        }

        console.log("Result save room", result);

        floor.rooms.push(room._id);

        floor.save(err => {
          console.log("Error save floor", err);
          if (err) {
            resolve(err);
          }
          console.log("Result save floor", result);
          resolve(result);
        });
      });
    })
  );
};
