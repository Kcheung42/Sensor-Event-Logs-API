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
   :name "living"
   :sensors []}) ;; list of sensors registered to the room

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
                (conj results @v)))
            []
            map)))

;; (defn get-all-sensors
;;   "Returns a dereferenced map of all sensors"
;;   []
;;   (map (fn[[k v] m]
;;          (deref v))
;; @sensor-map))

(defn get-all-events []
  @event-log)

(defn get-all-rooms []
  "Returns a dereferenced map of all rooms"
  (let [map @room-map]
    (reduce (fn[results room]
              (let [[k v] room]
                (conj results @v)))
            []
            map)))

;; (defn get-all-sensors
;;   "Returns a dereferenced map of all sensors"
;;   []
;;   (map (fn[[k v] m]
;;          (deref v))
;; @room-map))


(defn get-sensor-atom [id]
  ((keyword id) @sensor-map))

(defn get-event [id]
  ((keyword id) @event-log))

(defn get-room-atom [id]
  ((keyword id) @room-map))



;; ---- Settter -----

(defn create-event
  [id timestamp sensor-id status]
  (let [new-event {(keyword id) {:id id
                                 :timestamp timestamp
                                 :sensor-id sensor-id
                                 :status status}}]
    new-event))

(defn log-event
  "Check if the event with id exist. If not update event-log
  with new event with given parameters"
  [id timestamp sensor-id status]
  (if (and (not (event-exist? id))
           (sensor-exist? sensor-id))
    (let [new-event (create-event id timestamp sensor-id status)]
      (swap! event-log conj new-event))
    nil))

(defn register-room
  [id name]
  (if (and (not (room-exist? id)))
    (let [new-room {(keyword id) (atom {:id id
                                        :name name
                                        :sensors []})}]
      (swap! room-map conj new-room))
    nil))

;;---- Atom Watchers ----
;; Watchers are used to automatically create an event logs when a sensor atom status changes

(defn motion-alert
  [key watched old-stte new-state]
  (do
    (println "Motion detected")
    (log-event
     (uuid)
     (:last-updated new-state)
     (:id new-state)
     (:status new-state))))

(defn light-alert
  [key watched old-stte new-state]
  (let [lumen (:status new-state)]
    ;; (if (> lumen 100)
      (do
        (println "So much Light! Someone's in Here")
        (log-event
         (uuid)
         (:last-updated new-state)
         (:id new-state)
         (:status new-state)))))

(defn door-alert
  [key watched old-stte new-state]
  (let [lumen (:status new-state)]
      (do
        (println "Someone Used the Door")
        (log-event
         (uuid)
         (:last-updated new-state)
         (:id new-state)
         (:status new-state)))))

(def watchers
  {:light light-alert
   :motion motion-alert
   :door door-alert})

;;---- Atom Watchers End ----

;; ---- Setter ----
;; have to split setter section because register-sensor requires
;; watchers to be defined first
(defn create-sensor
  "creates an atom sensor "
  [id type room-id status]
  (let [type-kw (keyword type) ;; type may come in as text
        new-sensor (atom {:id id
                          :type type-kw
                          :room-id room-id
                          :status status
                          :last-updated (now)})
        new-entry {(keyword id) new-sensor}]
    (add-watch new-sensor :status-update (type-kw watchers))
    new-entry))

;; TODO
;; Probably want to use refs and dosync so we update the room's list of
;; sensor
(defn register-sensor
  "Adds a sensor (atom) to the sensor-map
  Return the sensor that was added"
  [id type room-id status]
  (if (and (not (sensor-exist? id))
           (room-exist? room-id))
    (let [new-entry (create-sensor id type room-id status)]

      ;; refs and dosync probably be better here.
      (swap! sensor-map conj new-entry)
      (swap! (get-room-atom room-id) update-in [:sensors] conj id)
      @(get-sensor-atom id))
    nil))




;; --- Update ----
(defn update-sensor-status [id timestamp status]
  (if-let [sensor (get-sensor-atom id)]
    (swap! sensor conj {:last-updated timestamp :status status})))

;; Testing code

;; (def sensor-1 (uuid))
(def room-1 (uuid))

(register-room room-1 "Bogus-Room-404")
(register-sensor "Bogus-Sensor" "light" room-1 1)
(update-sensor-status "Bogus-Sensor" (now) 102)
(update-sensor-status "Bogus-Sensor" (now) 101)
(get-all-events)

;; (get-sensor-atom "Bogus-Sensor")
;; (update-sensor-status sensor-1 (now) 101)
;; (get-all-events)
;; (get-atom-list-count event-log)
