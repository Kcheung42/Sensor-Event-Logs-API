# web-app

This is the Web Application to view the status of the sensors.

Upon Initialization, The app-db will be initialized with ajax calls to
the web-server.

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
