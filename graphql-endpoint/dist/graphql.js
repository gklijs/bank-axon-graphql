"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.startGraphQL = void 0;
/* eslint camelcase: ["error", {allow: ["max_items", "all_last_transactions", "transaction_by_id", "transactions_by_iban", "get_account", "money_transfer", "stream_transactions", "min_amount", "max_amount", "descr_includes"]}] */
const load_1 = require("@graphql-tools/load");
const graphql_file_loader_1 = require("@graphql-tools/graphql-file-loader");
const path_1 = require("path");
const schema_1 = require("@graphql-tools/schema");
const express_1 = __importDefault(require("express"));
const express_graphql_1 = __importDefault(require("express-graphql"));
const db_1 = require("./db");
const resolver_functions_1 = require("./resolver-functions");
function startGraphQL(transactionObservable, accountCreationFeedbackObservable, moneyTransferFeedbackObservable) {
    const schema = load_1.loadSchemaSync(path_1.join(__dirname, '../resource/bank.graphql'), {
        loaders: [
            new graphql_file_loader_1.GraphQLFileLoader()
        ]
    });
    const resolvers = {
        QueryRoot: {
            all_last_transactions() {
                return db_1.findAllLastTransactions();
            },
            transaction_by_id(obj, { id }) {
                return db_1.findTransactionById(id);
            },
            transactions_by_iban(obj, { iban, max_items }) {
                return db_1.findTransactionsByIban(iban, max_items);
            }
        },
        MutationRoot: {
            get_account(obj, { username, password }) {
                return resolver_functions_1.retrieveAccountFeedback(username, password, accountCreationFeedbackObservable);
            },
            money_transfer(obj, { amount, descr, from, to, token, username, uuid }) {
                return resolver_functions_1.retrieveMoneyFeedback(amount, descr, from, to, token, username, uuid, moneyTransferFeedbackObservable);
            }
        },
        SubscriptionRoot: {
            get_account(obj, { username, password }) {
                return resolver_functions_1.retrieveAccountFeedback(username, password, accountCreationFeedbackObservable);
            },
            money_transfer(obj, { amount, descr, from, to, token, username, uuid }) {
                return resolver_functions_1.retrieveMoneyFeedback(amount, descr, from, to, token, username, uuid, moneyTransferFeedbackObservable);
            },
            stream_transactions: {
                subscribe: (obj, { direction, iban, min_amount, max_amount, descr_includes }) => resolver_functions_1.streamTransactions(direction, iban, min_amount, max_amount, descr_includes, transactionObservable),
                resolve: (root) => {
                    console.info(`This was the root object: ${root}`);
                    console.info(`Type: ${typeof root}`);
                    return root;
                }
            }
        }
    };
    // Add resolvers to the schema
    const schemaWithResolvers = schema_1.addResolversToSchema({
        schema,
        resolvers
    });
    const app = express_1.default();
    app.use(express_graphql_1.default({
        schema: schemaWithResolvers,
        graphiql: true
    }));
    app.listen(8000, () => {
        console.info('Server listening on http://localhost:8000');
    });
}
exports.startGraphQL = startGraphQL;
//# sourceMappingURL=graphql.js.map