const Room = require("../models/Room");
const Floor = require("../models/Floor");
const Reservation = require("../models/Reservation");

module.exports.create = async (event, context) => {
  const body = JSON.parse(event.body);
  const floorName = body.floorName;
  const roomName = body.roomName;
  return new Promise((resolve, reject) =>
    Floor.findOne({ name: floorName })
      .select("_id")
      .exec((err, floor) => {
        if (err) {
          resolve(err);
        }
        Room.findOne({ name: roomName, floor: { _id: floor._id } }).exec(
          (err, room) => {
            if (err) {
              resolve(err);
            }
            const reservation = new Reservation({
              ...body.reservation,
              room
            });
            reservation.save((err, result) => {
              if (err) {
                resolve(err);
              }
              room.reservations.push(reservation._id);
              room.save(err => {
                if (err) {
                  resolve(err);
                }
                resolve(result);
              });
            });
          }
        );
      })
  );
};
