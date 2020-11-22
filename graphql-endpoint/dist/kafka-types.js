"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getName = exports.isSuccessMoneyTransfer = exports.isSuccessAccountCreation = void 0;
function isSuccessAccountCreation(item) {
    return item.iban !== undefined;
}
exports.isSuccessAccountCreation = isSuccessAccountCreation;
function isSuccessMoneyTransfer(item) {
    return item.reason === undefined;
}
exports.isSuccessMoneyTransfer = isSuccessMoneyTransfer;
function getName(command) {
    if (command.username !== undefined) {
        return 'ConfirmAccountCreation';
    }
    else {
        return 'ConfirmMoneyTransfer';
    }
}
exports.getName = getName;
//# sourceMappingURL=kafka-types.js.map