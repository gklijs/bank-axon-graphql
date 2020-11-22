"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.sendCommand = exports.connectProducer = exports.mtf = exports.acf = exports.bc = void 0;
const kafkajs_1 = require("kafkajs");
const dist_1 = require("@kafkajs/confluent-schema-registry/dist");
const kafkajs_lz4_1 = __importDefault(require("kafkajs-lz4"));
const kafka_types_1 = require("./kafka-types");
const rxjs_1 = require("rxjs");
kafkajs_1.CompressionCodecs[kafkajs_1.CompressionTypes.LZ4] = new kafkajs_lz4_1.default().codec;
const config = {
    clientId: 'my-app', brokers: ['localhost:9092', 'localhost:9094', 'localhost:9096']
};
const kafka = new kafkajs_1.Kafka(config);
const registry = new dist_1.SchemaRegistry({ host: 'http://localhost:8081/' });
const producer = kafka.producer();
const schemaIdCache = new Map();
async function consumeObservable(groupId, topic) {
    const consumer = kafka.consumer({ groupId: groupId });
    await consumer.connect();
    await consumer.subscribe({ topic: topic, fromBeginning: true });
    return new rxjs_1.Observable((subject) => {
        consumer.run({
            autoCommit: true,
            eachMessage: async ({ message }) => {
                const event = await registry.decode(message.value);
                subject.next(event);
            }
        });
    });
}
async function bc() {
    return consumeObservable('graphql-endpoint-bc', 'balance_changed');
}
exports.bc = bc;
async function acf() {
    return consumeObservable('graphql-endpoint-acf', 'account_creation_feedback');
}
exports.acf = acf;
async function mtf() {
    return consumeObservable('graphql-endpoint-mtf', 'money_transfer_feedback');
}
exports.mtf = mtf;
function getSubjectFromCommand(command) {
    return 'commands-nl.openweb.data.' + kafka_types_1.getName(command);
}
async function getSchemaId(subject) {
    const idFromCache = await schemaIdCache.get(subject);
    if (await idFromCache) {
        return idFromCache;
    }
    const idFromRegistry = await registry.getLatestSchemaId(subject);
    schemaIdCache.set(subject, idFromRegistry);
    return idFromRegistry;
}
async function connectProducer() {
    await Promise.all([
        producer.connect(),
        getSchemaId('commands-nl.openweb.data.ConfirmAccountCreation'),
        getSchemaId('commands-nl.openweb.data.ConfirmMoneyTransfer')
    ]);
}
exports.connectProducer = connectProducer;
async function sendCommand(user, command) {
    console.info(`sending command: ${JSON.stringify(command)}`);
    const schemaId = await getSchemaId(getSubjectFromCommand(command));
    const value = await registry.encode(schemaId, command);
    await producer.send({
        topic: 'commands',
        messages: [{ key: user, value: value }],
        compression: kafkajs_1.CompressionTypes.LZ4
    });
}
exports.sendCommand = sendCommand;
//# sourceMappingURL=kafka.js.map