"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const kafkajs_1 = require("kafkajs");
const dist_1 = require("@kafkajs/confluent-schema-registry/dist");
const kafkajs_lz4_1 = __importDefault(require("kafkajs-lz4"));
kafkajs_1.CompressionCodecs[kafkajs_1.CompressionTypes.LZ4] = new kafkajs_lz4_1.default().codec;
const config = {
    clientId: 'my-app', brokers: ['localhost:9092', 'localhost:9094', 'localhost:9096']
};
const kafka = new kafkajs_1.Kafka(config);
const registry = new dist_1.SchemaRegistry({ host: 'http://localhost:8081/' });
const consumer = kafka.consumer({ groupId: 'test-group' });
exports.default = () => __awaiter(void 0, void 0, void 0, function* () {
    yield consumer.connect();
    yield consumer.subscribe({ topic: 'balance_changed', fromBeginning: true });
    yield consumer.run({
        eachMessage: ({ topic, partition, message }) => __awaiter(void 0, void 0, void 0, function* () {
            const decodedKey = message.key.toString();
            const decodedValue = yield registry.decode(message.value);
            console.log({ decodedKey, decodedValue });
        }),
    });
});
//# sourceMappingURL=runKafka.js.map