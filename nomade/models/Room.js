const mongoose = require("mongoose");

const Room = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  capacity: {
    type: Number,
    required: true
  },
  floor: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Floor"
  },
  reservations: [{ type: mongoose.Schema.Types.ObjectId, ref: "Reservation" }]
});

module.exports = mongoose.model("Room", Room);
