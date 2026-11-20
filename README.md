#  CarbonWatch

**A multi-threaded client–server CO₂ monitoring system in Java, using TCP sockets.**

CarbonWatch lets residents submit CO₂ readings for their area to a central server over the network. The server handles several clients at the same time with a thread pool, validates every reading and logs it safely to a shared CSV file, ready for analysis.

##  Features

- **Client–server over TCP sockets.** A custom text-based protocol guides the user through each submission.
- **Concurrent clients.** A fixed thread pool (`ExecutorService`) serves up to 4 clients at once.
- **Thread-safe logging.** A `ReentrantLock` makes sure simultaneous writes never corrupt the CSV file.
- **Input validation:**
  - User ID can't be empty
  - UK postcode format checked with a regex (e.g. `CF10 1AA`)
  - CO₂ value must be between 250 and 5000 ppm
- **Graceful shutdown.** A JVM shutdown hook closes the server cleanly.
- **Configurable** host, port and CSV path from the command line.
- Clients can type `QUIT` at any prompt to disconnect.

##  Architecture

    src/
    ├── common/
    │   ├── CO2Reading.java     # data model with validation and CSV conversion
    │   └── Protocol.java       # shared protocol messages
    ├── server/
    │   ├── CO2Server.java      # accepts connections, manages the thread pool
    │   ├── ClientHandler.java  # one handler per connected client
    │   └── DataLogger.java     # thread-safe CSV writer
    ├── client/
    │   └── CO2Client.java      # console client
    └── test/
        └── SystemTest.java     # validation and CSV tests

**Flow:** client connects → server sends welcome → asks for User ID, postcode and CO₂ reading → validates → logs to `data/co2_readings.csv` → confirms and closes the connection.

##  Getting started

You need Java 11 or newer.

    # compile
    javac -d bin src/common/*.java src/server/*.java src/client/*.java src/test/*.java

    # 1. start the server (default port 8888)
    java -cp bin server.CO2Server

    # 2. in one or more new terminals, start clients
    java -cp bin client.CO2Client

    # run the tests
    java -cp bin test.SystemTest

Optional arguments:

    java -cp bin server.CO2Server <port> <csv-file>
    java -cp bin client.CO2Client <host> <port>

##  Example output (CSV)

    Timestamp,UserID,Postcode,CO2_ppm
    2025-11-16 11:48:43,RES001,CF10 1AA,415.50

##  Future improvements

- Live dashboard showing readings on a map by postcode
- Store readings in a database instead of CSV
- Encrypted connections (TLS) and user authentication
- Automated JUnit test suite

## 👤 Author

**Ahsan**, BSc (Hons) Computer Science, Cardiff Metropolitan University
