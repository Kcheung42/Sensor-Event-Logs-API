(ns web-server-restful.core)

;; ---- Helper functions -----
(defn now [] (new java.util.Date))

(defn uuid
  "Generate unique id"
  [] (str (java.util.UUID/randomUUID)))


;; What our data should look like

(def sensor
  "example sensor"
  {:id "sensor-uuid-1"
   :type :motion
   :room-id "rooom-uuid-1"
   :status 1})

(def room
  "example room"
  {:id "room-uuid-1"
   :name "living"})

(def event
  "example room"
  (let [date (now)]
    {:id "event-uuid-1" :timestamp date :sensor-id "sensor-uuid-1"}))
;; => {:id "event-uuid-1", :timestamp #inst "2018-10-17T05:53:53.907-00:00", :sensor-id "sensor-uuid-1"}


;; ----- Database/identities----
(def event-log (atom {}))

(def sensor-map (atom {}))

(def room-map (atom {}))

;; I think every sensor created here should be an atom with an attached,
;; watcher to log an event whenever its state get's changed

;; (def db
;;   {:events event-log
;; :sensors sensor-map})

(def sensor-types #{:motion :light :door})


;; ---- Validation ----

(defn sensor-exist? [id]
  (contains? @sensor-map (keyword id)))

(defn event-exist? [id]
  (contains? @event-log (keyword id)))

(defn valid-event? [id]
  "TODO"
  true)

(defn room-exist? [id]
  (contains? @room-map (keyword id)))

;; ---- Getter -----
(defn get-atom-list-count [atom]
  (count @atom))

(defn get-all-sensors
  "Returns a dereferenced map of all sensors"
  []
  (let [map @sensor-map]
    (reduce (fn[results sensor]
              (let [[k v] sensor]
                (conj results {k @v})))
            {}
            map)))

(defn get-all-events []
  @event-log)

(defn get-all-rooms []
  @room-map)


(defn get-sensor-atom [id]
  ((keyword id) @sensor-map))

(defn get-event [id]
  ((keyword id) @event-log))

(defn get-room [id]
  ((keyword id) @room-map))



;; ---- Settter -----


(defn log-event
  "Check if the event with id exist. If not update event-log
  with new event with given parameters"

  [id timestamp sensor-id status]
  (if (and (not (event-exist? id))
           (sensor-exist? sensor-id))
    (let [new-event {(keyword id) {:id id
                                   :timestamp timestamp
                                   :sensor-id sensor-id
                                   :status status}}]
      (swap! event-log conj new-event))
    nil))

(defn register-room
  [id name]
  (if (and (not (room-exist? id)))
    (let [new-room {(keyword id) {:id id
                                  :name name}}]
      (swap! room-map conj new-room))
    nil))

;;---- Atom Watchers ----
;; Watchers are Used to create an event log when a sensor status changes

(defn motion-alert
  [key watched old-stte new-state]
  (do
    (println "motion detected")
    (log-event
     (uuid)
     (:last-updated new-state)
     (:id new-state)
     (:status new-state))))

(defn light-alert
  [key watched old-stte new-state]
  (let [lumen (:status new-state)]
    (if (> lumen 100)
      (do
        (println "lumens over threshold, logging event")
        (log-event
         (uuid)
         (:last-updated new-state)
         (:id new-state)
         (:status new-state))))))

(defn door-alert
  [key watched old-stte new-state]
  (let [lumen (:status new-state)]
    (if (> lumen 100)
      (do
        (println "lumens over threshold, logging event")
        (log-event
         (uuid)
         (:last-updated new-state)
         (:id new-state)
         (:status new-state))))))

(def watchers
  {:light light-alert})

;;---- Atom Watchers End ----

;; ---- Setter ----
;; have to split setter section because register-sensor requires
;; watchers to be defined first


(defn register-sensor
  "Adds a sensor (atom) to the sensor-map
  Return the sensor that was added"

  [id type room-id status]
  (if (and (not (sensor-exist? id))
           (room-exist? room-id))
    (let [type-kw (keyword type) ;; type may come in as text
          new-sensor (atom {:id id
                            :type type-kw
                            :room-id room-id
                            :status status
                            :last-updated (now)})
          new-entry {(keyword id) new-sensor}]
      (add-watch new-sensor :status-update (type-kw watchers))
      (swap! sensor-map conj new-entry)
      @(get-sensor-atom id))
    nil))




;; --- Update ----
(defn update-sensor-status [id timestamp status]
  (swap! (get-sensor-atom id) conj {:last-updated timestamp :status status}))

;; Testing code

;; (def sensor-1 (uuid))
;; (def sensor-2 (uuid))
;; (def room-1 (uuid))

;; (register-room room-1 "my-first-room")
;; (register-sensor sensor-1 "light" room-1 1)
;; (register-sensor sensor-2 "door" room-1 1)
;; (get-sensor-atom sensor-1)
;; (update-sensor-status sensor-1 (now) 101)
;; (update-sensor-status sensor-1 (now) 102)
;; (get-all-events)
;; (get-atom-list-count event-log)
