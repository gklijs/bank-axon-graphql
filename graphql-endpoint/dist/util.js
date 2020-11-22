"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.uuidStringToBytes = exports.uuidBytesToString = void 0;
const uuid_1 = require("uuid");
const uuidParse = require('uuid-parse');
function uuidBytesToString(buffer) {
    const v4options = {
        random: buffer
    };
    return uuid_1.v4(v4options);
}
exports.uuidBytesToString = uuidBytesToString;
function uuidStringToBytes(uuid) {
    return uuidParse.parse(uuid);
}
exports.uuidStringToBytes = uuidStringToBytes;
//# sourceMappingURL=util.js.map