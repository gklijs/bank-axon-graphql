"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const kafka_1 = require("./kafka");
const operators_1 = require("rxjs/operators");
const db_1 = require("./db");
const graphql_1 = require("./graphql");
const util_1 = require("./util");
let transactionObservable;
let accountCreationFeedbackObservable;
let moneyTransferFeedbackObservable;
const init = async () => {
    await Promise.all([
        kafka_1.connectProducer(),
        transactionObservable = (await kafka_1.bc()).pipe(operators_1.concatMap(balanceChanged => {
            return db_1.storeTransaction(balanceChanged);
        })).pipe(operators_1.share()),
        accountCreationFeedbackObservable = (await kafka_1.acf()).pipe(operators_1.share()),
        moneyTransferFeedbackObservable = (await (kafka_1.mtf())).pipe(operators_1.share())
    ]);
};
init().then(() => {
    transactionObservable
        .subscribe(transaction => console.log(`Transaction with id ${transaction.id} stored`));
    accountCreationFeedbackObservable
        .subscribe(feedback => console.log(`Account creation feedback with id ${util_1.uuidBytesToString(feedback.id)} received`));
    moneyTransferFeedbackObservable
        .subscribe(feedback => console.log(`Money transfer feedback with id ${util_1.uuidBytesToString(feedback.id)} received`));
    graphql_1.startGraphQL(transactionObservable, accountCreationFeedbackObservable, moneyTransferFeedbackObservable);
});
//# sourceMappingURL=app.js.map