const Floor = require("../models/Floor");
const mongoose = require("mongoose");

let cachedDb = null;

module.exports.connectToDatabase = uri => {
  console.log("=> connect to database");

  if (cachedDb) {
    console.log("=> using cached database instance");
    return Promise.resolve(cachedDb);
  }

  return mongoose.connect(uri, { useNewUrlParser: true }).then(db => {
    cachedDb = db;
    return cachedDb;
  });
};

module.exports.queryDatabase = db => {
  console.log("=> query database");
  return Floor.find({}, (err, result) => {
    console.log(result);
  });
};
