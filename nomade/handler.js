"use strict";

const util = require("./misc/util");
const constants = require("./misc/constants");

const Floor = require("./models/Floor");
const Room = require("./models/Room");
const Reservation = require("./models/Reservation");

const FloorController = require("./controllers/FloorController");
const RoomController = require("./controllers/RoomController");
const ReservationController = require("./controllers/ReservationController");

module.exports.floors = async (event, context) => {
  await util.connectToDatabase(constants.MONGODB_URI);

  switch (event.httpMethod) {
    case "GET":
      try {
        let result = await FloorController.find(event, context);
        console.log(result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: "Floor find:",
            floor: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 500,
          body: {
            error: e
          }
        };
      }
    case "POST":
      try {
        let result = await FloorController.create(event, context);
        console.log(result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: "Floor create:",
            floor: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 500,
          body: {
            error: e
          }
        };
      }
    default:
      return {
        statusCode: 404
      };
  }
};

module.exports.rooms = async (event, context) => {
  await util.connectToDatabase(constants.MONGODB_URI);

  switch (event.httpMethod) {
    case "GET":
      try {
        let result = await RoomController.findAll(event, context);
        console.log(result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: "Room find:",
            rooms: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 500,
          body: {
            error: e
          }
        };
      }
    case "POST":
      try {
        let result = await RoomController.create(event, context);
        console.log("RoomController done", result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: "Room create:",
            room: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 500,
          body: {
            error: e
          }
        };
      }
    default:
      return {
        statusCode: 404
      };
  }
};

module.exports.reservations = async (event, context) => {
  await util.connectToDatabase(constants.MONGODB_URI);

  switch (event.httpMethod) {
    case "POST":
      try {
        let result = await ReservationController.create(event, context);
        console.log(result);
        return {
          statusCode: 200,
          body: JSON.stringify({
            message: "Reservation create:",
            reservation: result
          })
        };
      } catch (e) {
        console.log(e);
        return {
          statusCode: 500,
          body: {
            error: e
          }
        };
      }
    default:
      return {
        statusCode: 404
      };
  }
};
