(ns web-server-restful.core
  (:require [ring.adapter.jetty :as jetty]))

;; ---- Helper functions -----
(defn get_date []
  (java.util.Date))

;; ----- Database/identities----
(def valid-sensor-keys
  [:id :type :room-id :status])

(def valid-event-keys
  [:id :type :room-id :status])

(def sensor
  "example sensor"
  {:id "1" :type :motion :room-id "1" :status 1})

(def room
  "example room"
  {:id "1" :name "living"})

(def event
  "example room"
  {:id "1" :timestamp get_date :sensor-id "1"})

(def event-log (atom []))

(def sensor-list (atom []))

(def db
  {:events event-log
   :sensors sensor-list})

(def sensor-types #{:motion :light :door})


;; ---- Validators ----

(defn sensor-exist? [id]
  (contains? (set (map :id @sensor-list)) id))

(defn event-exist? [id]
  (contains? (set (map :id @event-log)) id))

(defn valid-sensor?
  "Assuming sensor is valid"
  [sensor]
  (let [keys (set (keys sensor))
        result (set (map #(contains? keys %) valid-sensor-keys))]
    (if (contains? result false)
      false
      true)))

(defn valid-event? [id]
  "TODO"
  true)

;; ---- Settter -----

(defn create-sensor [new-sensor]
  (if (and (valid-sensor? new-sensor)
           (not (sensor-exist? (:id new-sensor))))
    (swap! sensor-list conj new-sensor)
    nil))

(defn create-event [new-event]
  (if (and (valid-event? new-event)
           (not (event-exist? (:id new-event))))
    (swap! event-log conj new-event)))

;; (defn create-room [])

;; create-sensor and create-event looks simlar. TODO Optimize


;; ---- Getter -----
(defn get-atom-list-count [atom]
  (count @atom))

(defn get-all-sensors []
  @sensor-list)

(defn get-all-events []
  @sensor-list)

(defn app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain;=us-ascii"}
   :body (str request)})

;; (defn foo
;;   "I don't do a whole lot."
;;   [x]
;;   (println x "Hello, World!"))
