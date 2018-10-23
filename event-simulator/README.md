# event-simulator

An simulator for sensor events that will change the status of a sensor stored in the web-server

## Usage

```
lein run
```

### Features

- Creates a list of Rooms and sends a request to the web-server to register it into the database

- Creates a list of sensors assigned to a room from the list created in above and registers it into the database

- Spawn a Go block Loop (infinite) for each sensor and wait between calls. Waiting time is a random value between the sensor's interval range.

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
