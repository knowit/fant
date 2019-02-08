const mongoose = require('mongoose');

const NFC = new mongoose.Schema({
  key: {
    type: String,
    required: true,
  },
});

module.exports = mongoose.model('NFC', NFC);
