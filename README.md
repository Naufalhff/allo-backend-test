# Split Bill API

A Spring Boot REST API that helps a group of people manage shared expenses and calculate who owes whom at the end. Built for the Allo Bank Engineering Technical Test.

## Prerequisites

- Docker and Docker Compose

## How to Build and Run

This project uses a multi-stage Docker build and is bundled with a PostgreSQL database via `docker compose`. You do not need Java or Maven installed on your host machine.

Simply run the provided startup scripts in the root directory:

- For Mac/Linux: `./run.sh`
- For Windows: Double-click `run.bat` or type `run.bat` in the terminal.

Alternatively, you can manually run:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

## Example cURL Commands

### 1. Create a Bill Group

```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekend Getaway",
    "participants": ["Andi", "Budi", "Citra"]
}'
```

### 2. Add an Expense

Assuming the group ID is `1` and Andi's participant ID is `1`:

```bash
curl -X POST http://localhost:8080/api/groups/1/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Seafood Dinner",
    "amount": 300000.00,
    "paidById": 1
}'
```

### 3. Retrieve Settlement Summary

```bash
curl -X GET http://localhost:8080/api/groups/1/settlement
```

## Personalization

- **GitHub Username:** `naufalhff`
- **Calculated Service Charge Value:** **9%**
  - ASCII Calculation: n(110) + a(97) + u(117) + f(102) + a(97) + l(108) + h(104) + f(102) + f(102) = 939
  - 939 % 10 = 9

## Submission Question Answer

_What was the hardest design decision you made while building this, and what trade-off did you accept?_

The hardest decision was choosing how to implement the debt settlement matching algorithm. I could have implemented a complex graph flow algorithm to find the absolute mathematical minimum number of transactions, but instead, I opted for a greedy matching approach (sorting positive balances and negative balances and matching them incrementally). The trade-off is that while it may occasionally result in slightly more transactions in highly complex, massive groups, it drastically reduces the algorithmic time complexity (O(N) instead of O(N^3)) and keeps the codebase clean, readable, and highly maintainable for the scope of typical bill-splitting scenarios. I also rigidly enforced `BigDecimal` usage to prevent any floating-point arithmetic precision loss.
