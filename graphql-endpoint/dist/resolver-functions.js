"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.streamTransactions = exports.retrieveMoneyFeedback = exports.retrieveAccountFeedback = void 0;
const rxjs_1 = require("rxjs");
const kafka_types_1 = require("./kafka-types");
const db_1 = require("./db");
const util_1 = require("./util");
const kafka_1 = require("./kafka");
const operators_1 = require("rxjs/operators");
function firstMatch(id, observable) {
    return observable
        .pipe(operators_1.filter(feedback => feedback.id.equals(id)))
        .pipe(operators_1.first())
        .pipe(operators_1.timeout(5000), operators_1.catchError(() => rxjs_1.of('kafka timed out')))
        .toPromise();
}
async function retrieveAccountFeedback(username, password, accountCreationFeedbackObservable) {
    const uuidString = await db_1.checkPassword(username, password);
    if (uuidString === null) {
        return { reason: 'password invalid' };
    }
    const uuidBytes = await util_1.uuidStringToBytes(uuidString);
    const uuidBuffer = Buffer.from(uuidBytes);
    await kafka_1.sendCommand(username, {
        id: uuidBuffer,
        username: username
    });
    const kafkaResponse = await firstMatch(uuidBuffer, accountCreationFeedbackObservable);
    if (typeof kafkaResponse === 'string') {
        return { reason: kafkaResponse };
    }
    else if (kafka_types_1.isSuccessAccountCreation(kafkaResponse)) {
        const confirmed = kafkaResponse;
        return {
            iban: confirmed.iban,
            token: confirmed.token
        };
    }
    else {
        await db_1.removeAccount(uuidString);
        return {
            reason: kafkaResponse.reason
        };
    }
}
exports.retrieveAccountFeedback = retrieveAccountFeedback;
async function retrieveMoneyFeedback(amount, descr, from, to, token, username, uuid, moneyTransferFeedbackObservable) {
    const buffer = Buffer.from(util_1.uuidStringToBytes(uuid));
    await kafka_1.sendCommand(username, {
        id: buffer,
        token: token,
        amount: amount,
        from: from,
        to: to,
        description: descr
    });
    const kafkaResponse = await firstMatch(buffer, moneyTransferFeedbackObservable);
    if (typeof kafkaResponse === 'string') {
        return { reason: kafkaResponse, success: false, uuid: uuid };
    }
    else if (kafka_types_1.isSuccessMoneyTransfer(kafkaResponse)) {
        return { success: true, uuid: uuid };
    }
    else {
        return {
            reason: kafkaResponse.reason,
            success: false,
            uuid: uuid
        };
    }
}
exports.retrieveMoneyFeedback = retrieveMoneyFeedback;
function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
function streamTransactions(direction, iban, minAmount, maxAmount, descrIncludes, transactionObservable) {
    const asyncCounterIterator = () => {
        let iter = 1;
        let exhausted = false;
        return {
            async next() {
                if (iter > 10 || exhausted) {
                    return { done: true };
                }
                await delay(5000);
                const iteratorResult = {
                    value: {
                        iban: 'iban' + iter,
                        new_balance: '$10,10',
                        changed_by: 'someone else' + iter,
                        from_to: '$11,10',
                        direction: 'DEBIT',
                        descr: 'some description ' + iter,
                        id: iter
                    },
                    done: false
                };
                iter++;
                return iteratorResult;
            },
            async throw(e) {
                console.log('oops something is wrong');
                throw e;
            },
            async return() {
                exhausted = true;
                console.log('I have been released !!!');
                return { done: true };
            }
        };
    };
    return () => ({
        [Symbol.asyncIterator]() {
            return asyncCounterIterator();
        }
    });
}
exports.streamTransactions = streamTransactions;
//# sourceMappingURL=resolver-functions.js.map