(ns web-server-restful.core)

;; ---- Helper functions -----
(defn now [] (new java.util.Date))

(defn uuid
  "Generate unique id"
  [] (str (java.util.UUID/randomUUID)))

;; ----- Database/identities----

;; to delete
(def valid-event-keys
  [:id :type :room-id :status])

(def sensor
  "example sensor"
  {:id "1"
   :type :motion
   :room-id "1"
   :status 1})

(def room
  "example room"
  {:id "1"
   :name "living"})

(def event
  "example room"
  (let [date (now)]
    {:id "1" :timestamp date :sensor-id "1"}))
;; => {:id 1, :timestamp #inst "2018-10-11T23:28:40.343-00:00", :sensor-id 1}

(def event-log (atom {}))

(def sensor-map (atom {}))

(def room-map (atom {}))

;; I think every sensor created here should be an atom with an attached,
;; watcher to log an evekt whenever its state get's changed

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

(defn room-exists? [id]
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
  (if (and (not (room-exists? id)))
    (let [new-room {(keyword id) {:id id
                                  :name name}}]
      (swap! room-map conj new-room))
    nil))

;;---- Atom Watchers ----

(defn motion-alert
  [key watched old-stte new-state]
  (do
    (println "motion detected")
    (log-event
     (uuid)
     (:timestamp new-state)
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
         (:timestamp new-state)
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
         (:timestamp new-state)
         (:id new-state)
         (:status new-state))))))

(def watchers
  {:light light-alert})

;;---- Atom Watchers End ----

;; ---- Setter ----
;; have to split setter section because register-sensor requires
;; watchers to be defined first


(defn register-sensor
  "Check if the sensor with id exist. If not update sensor-map
  with new sensor with given parameters"

  [id type room-id status]
  (if (not (sensor-exist? id))
    (let [type-kw (keyword type) ;; type may come in as text
          new-sensor (atom {:id id
                            :type type-kw
                            :room-id room-id
                            :status status
                            :last-updated (now)})
          new-entry {(keyword id) new-sensor}]
      (add-watch new-sensor :status-update (type-kw watchers))
      (swap! sensor-map conj new-entry))
    nil))




;; --- Update ----
(defn update-sensor-status [id timestamp status]
  (swap! (get-sensor-atom id) conj {:last-updated timestamp :status status}))

;; Testing code

(def sensor-1 (uuid))
(def sensor-2 (uuid))

(register-sensor sensor-1 "light" "room-1" 1)
(register-sensor sensor-2 "door" "room-1" 1)
(get-sensor-atom sensor-1)
;; (get-sensor-atom "7c2f63ed-aebd-43ea-8399-094b330f5a3b")
;; (update-sensor-status sensor-1 (now) 101)


;; ;; (log-event event-1 (now) sensor-1 1)
;; (get-all-events)


;; (def a (atom {:a 1
;;               :b 2
;;               :c 3}))
;; (swap! a conj {:a 2 :c 4})
;; (identity @a)
