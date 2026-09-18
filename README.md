# HouseBooking — Distributed Room Rental System

A room rental service in the style of Airbnb, built for the Distributed Systems
course (3664) at Athens University of Economics and Business, March 2024.

Three-person project.

## Architecture

A Master-Worker system communicating over TCP sockets.

    ConsoleApp / Dummy (clients)
            │  TCP
            ▼
         Master  :8000
            │  distributes by hash(room name)
            ├──────────┬──────────┐
            ▼          ▼          ▼
        Worker      Worker     Worker
         :8001       :8002      :8003
            └──────────┴──────────┘
                       │
                    Reducer
                       │
                    results

**Master** accepts client connections, hashes each room's name to decide which
worker owns it, forwards requests, and collects results.

**Workers** hold their share of the rooms in memory and run the map stage:
filtering by area, date range, price, guest capacity and star rating.

**Reducer** aggregates the per-worker results before they are returned to the
client.

## What it does

- Property managers list rooms and view booking statistics by area
- Renters search with multiple criteria at once, book a room, and leave a rating
- Bookings and ratings update the owning worker's state

## Implementation notes

- **Concurrency:** `synchronized` blocks with `wait()`/`notify()` coordinate
  access to shared room state while multiple client threads are served.
- **Partitioning:** rooms are assigned to workers by hashing the room name, so
  any node can compute which worker owns a given room without a lookup table.
- **Serialization:** JSON over the socket connection, using Gson.
- **Clients:** `ConsoleApp` is the interactive CLI; `Dummy` is a multi-threaded
  client used to exercise the system with concurrent requests.

## Files

| File | Role |
|---|---|
| `Master.java` | Accepts clients, partitions work across workers |
| `Worker.java` | Holds a shard of rooms, runs the map stage |
| `Reducer.java` | Aggregates worker results |
| `Filter.java` | Multi-criteria search predicates |
| `Room.java`, `Request.java` | Domain and protocol types |
| `JsonConverter.java` | Gson serialisation helpers |
| `ConsoleApp.java` | Interactive client |
| `Dummy.java` | Multi-threaded test client |

## Running

Configuration lives in `workers.json` (one master on 8000, three workers on
8001–8003) and `Room.json` holds the seed room data.

```bash
cd backend
mvn compile
```

Start the master, then each worker, then a client.
