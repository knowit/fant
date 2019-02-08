const mongoose = require("mongoose");

const Floor = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  rooms: [{ type: mongoose.Schema.Types.ObjectId, ref: "Room" }]
});

module.exports = mongoose.model("Floor", Floor);
