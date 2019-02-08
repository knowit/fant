const mongoose = require("mongoose");

const Reservation = new mongoose.Schema({
  from: {
    type: Date,
    required: true
  },
  to: {
    type: Date,
    required: true
  },
  reservedBy: {
    type: String,
    required: true
  },
  room: { type: mongoose.Schema.Types.ObjectId, ref: "Room" }
});

module.exports = mongoose.model("Reservation", Reservation);
