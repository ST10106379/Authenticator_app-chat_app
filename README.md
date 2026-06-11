# QuickChat — PROG5121 POE

A console-based Java chat application built across three parts as the
PROG5121 Portfolio of Evidence.

**Student:** ST10106379
**Module:** PROG5121 — Programming 1A
**Build tool:** Maven
**Tested on:** Java 25

---

## What it does

QuickChat is a small command-line chat app. The user registers, logs in,
and can then compose, send, store, disregard, search and report on messages.

### Features by part

**Part 1 — Authentication**
- Username, password and phone-number registration with complexity rules
- Login that validates the entered credentials against the registered ones

**Part 2 — Sending Messages**
- A `Message` class with auto-generated 10-digit message IDs and hashes
- 250-character message length validation
- Recipient validation (international code required)
- Three actions per message: Send / Disregard / Store-for-later
- Stored messages are written to `stored_messages.json`

**Part 3 — Store Data and Display Task Report**
- Five arrays: Sent, Disregarded, Stored, Message Hashes, Message IDs
- Reports & Search menu with six operations:
  1. Display sender and recipient of all sent messages
  2. Display the longest message (across sent + stored)
  3. Search by Message ID
  4. Search by Recipient
  5. Delete a message by its hash
  6. Display a full report of all sent messages
- Stored messages are loaded back from `stored_messages.json` on startup
- A "Load assignment test data" option populates the arrays with the five
  test messages from the brief, ready for the marker to explore

---

## Getting started

### Prerequisites
- Java 25 (any modern JDK distribution — Temurin / Oracle / OpenJDK)
- Maven 3.8+

### Build and run

```bash
cd Authenticator_App
mvn compile
mvn exec:java
```

Then follow the on-screen prompts:
1. Register a username (≥8 chars, contains `_`)
2. Register a password (≥8 chars, capital + lowercase + number + special)
3. Enter your first name
4. Log in with the same username + password
5. Enter a 10-digit phone number
6. The QuickChat menu opens

### Run the tests

```bash
cd Authenticator_App
mvn test
```

All Part 2 and Part 3 unit tests should pass.

---

## Demo flow for the marker

For a quick walk-through of every feature:

1. Register and log in (any valid credentials)
2. From the QuickChat menu → **3) Reports & Search → 7) Load assignment test data**
3. Now explore the report options:
   - Option 1 shows senders and recipients of the two sent messages
   - Option 2 returns *"Where are you? You are late!..."* as the longest
   - Option 3 — search for ID `0838884567` returns Message 4
   - Option 4 — search for `+27838884567` returns Messages 2 and 5
   - Option 5 — delete by hash works against the stored Message 2
   - Option 6 displays the full report

---

## Project structure

```
Authenticator_App/
├── pom.xml
├── src/
│   ├── main/java/com/mycompany/authenticator_app/
│   │   ├── Authenticator_App.java   # Entry point (registration + login)
│   │   ├── QuickChat.java           # Post-login menu and message flow
│   │   ├── Message.java             # Message class + all Part 3 operations
│   │   └── CellphoneNumber.java     # (placeholder)
│   └── test/java/
│       ├── Authenticator_AppTest.java   # Part 1 tests
│       └── MessageTest.java             # Part 2 + Part 3 tests
└── stored_messages.json    # Auto-generated; ignored by git
```

---

## Continuous integration

GitHub Actions runs the test suite on every push to `main` and every PR
(`.github/workflows/ci.yml`).

---

## Notes on design choices

- **Arrays** are implemented as `ArrayList<Message>`, the standard
  resizable array structure in Java. Getters return defensive copies so
  the internal state can't be mutated externally.
- **JSON parsing** for stored messages uses a small regex-based reader
  rather than an external library, keeping `pom.xml` dependency-free.
- **The "Developer" row in Test Data Message 4** (`0838884567`) is treated
  as the message ID, since the search-by-ID unit test in the brief uses
  the same value and expects Message 4 in return.
