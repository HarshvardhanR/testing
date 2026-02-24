/**
 * Piggymetrics MongoDB Initialization Script
 * Targets the 'finedge' database for the Account Service
 */

// 1. Switch to the target database
db = db.getSiblingDB('finedge');

print('--- Starting Data Dump for Account Service ---');

// 2. Insert or Update the 'demo' account
db.accounts.updateOne(
    { "_id": "demo" },
    {
        $set: {
            "_id": "demo",
            "last_seen": new Date(), // Using snake_case to match our @Field annotation
            "note": "This is a pre-filled demo account for FineEdge testing.",
            "expenses": [
                { "title": "Rent", "amount": 1300, "currency": "USD", "period": "MONTH", "icon": "home" },
                { "title": "Utilities", "amount": 120, "currency": "USD", "period": "MONTH", "icon": "utilities" },
                { "title": "Meal", "amount": 20, "currency": "USD", "period": "DAY", "icon": "meal" },
                { "title": "Gas", "amount": 240, "currency": "USD", "period": "MONTH", "icon": "gas" },
                { "title": "Vacation", "amount": 3500, "currency": "EUR", "period": "YEAR", "icon": "island" },
                { "title": "Phone", "amount": 30, "currency": "EUR", "period": "MONTH", "icon": "phone" },
                { "title": "Gym", "amount": 700, "currency": "USD", "period": "YEAR", "icon": "sport" }
            ],
            "incomes": [
                { "title": "Salary", "amount": 42000, "currency": "USD", "period": "YEAR", "icon": "wallet" },
                { "title": "Scholarship", "amount": 500, "currency": "USD", "period": "MONTH", "icon": "edu" }
            ],
            "saving": {
                "amount": 5900,
                "currency": "USD",
                "interest": 3.32,
                "deposit": true,
                "capitalization": false
            }
        }
    },
    { upsert: true }
);

print('--- Data Dump Complete: Demo account "demo" is ready ---');