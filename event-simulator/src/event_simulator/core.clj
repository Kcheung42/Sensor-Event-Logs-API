(ns event-simulator.core
  (:require
   [clj-http.client :as client]
   [cheshire.core :refer :all]
   ;; [clojure.core.async :refer :all]
   [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread
            alts! alts!! timeout go-loop]]
   ))



;; ---- Constants and Storage-----

;; Atoms to store list of entities

;; store a map where
;; key: is the same as uuid,
;; value: as the entitiy map
(def room-list (atom {}))
(def sensor-list (atom {}))

(def room-types
  #{"living" "media" "bathroom-1" "bathroom-2" "front-door" "back-door"})

;; a map of sensors where:
;; key: type of sensor
;; value: tuple of the a range of when sensor will activate
;;        this is to simulate when sensor is activated and sends
;;        data to the server
(def sensor-types
  {"motion" [10 20]
   "light" [20 30]
   "door" [30 60]})

;; --- Helper functions

(defn now [] (new java.util.Date))

(defn get_date []
  (java.util.Date))

(defn safe-println [& more]
  (.write *out* (str (clojure.string/join " " more) "\n")))


(defn uuid
  "Generate unique id"
  [] (str (java.util.UUID/randomUUID)))

(defn get-random-room-id []
  (:id (rand-nth @room-list)))


(defn get-random-sensor-type []
  (rand-nth (vec sensor-types)))

(defn rand-interval
  "Given a tuple of an interval range
  Return a random integer in range"
  [interval]
  (let [min (first interval)
        max (second interval)
        diff (- max min)]
    (+ (rand-int (+ 1 diff)) min)))
;; (rand-interval [6 10])
;; => 8

;; --- Create Sensors and Rooms ----

(defn make-one-room [room]
  (let [id (uuid)]
    {:id id
     :name room}))

(defn make-5-rooms []
  (reduce (fn [result room]
            (conj result (make-one-room room)))
          []
          room-types))

;; Sample Output
(make-5-rooms)
#_[{:id "8fb088ba-11c4-4d1c-83c4-d989acbf3a27", :name "living"}
   {:id "2896082b-c269-4c7d-a323-6c427dd7ac8c", :name "media"}
   {:id "7025dcf4-4f4f-4636-b675-b7158506ab02", :name "bathroom-1"}
   {:id "976c3db1-bdea-4c3d-b191-0c22d6f6ea4f", :name "bathroom-2"}
   {:id "2dc62479-001b-4ddf-93f8-38d17e6f220f", :name "front-door"}
   {:id "2ec2725e-24e4-4134-8de1-b58592a54dd1", :name "back-door"}]

(defn make-one-sensor [sensor-type room-id]
  (let [id (uuid)
        [type interval] sensor-type]
    {:id id
     :type type
     :room-id room-id
     :status 1
     :interval interval}))

;; Testing code. To Remove
;; (get-random-room-id)
;; (rand-nth (vec sensor-types))
;; (make-one-sensor (rand-nth (vec sensor-types)) (get-random-room-id))
;; (conj [] (make-one-sensor (rand-nth (vec sensor-types)) (get-random-room-id)))

(defn make-n-random-sensors [n]
  (if (not (empty? @room-list))
    (reduce (fn [result type]
              (conj result (make-one-sensor type (get-random-room-id))))
            []
            (repeatedly n #(rand-nth (vec sensor-types))))
    nil))
;; Sample Output
;; (make-n-random-sensors 3)
#_[{:id "bed73068-fdfe-4c27-a176-30002ad2354a",
    :type "door",
    :room-id :name,
    :status 1,
    :interval [60 120]}
   {:id "32911386-c2aa-4a14-82c9-965aa5be50f9",
    :type "light",
    :room-id :id,
    :status 1,
    :interval [10 30]}
   {:id "6ec48e99-2647-464e-b0d2-ee7af0c51136",
    :type "light",
    :room-id :name,
    :status 1,
    :interval [10 30]}]

;; ---- Update list functions ---

(defn update-room-list [list-of-rooms]
  (swap! room-list concat list-of-rooms))

(defn update-sensor-list [list-of-sensors]
  (swap! sensor-list concat list-of-sensors))

;; -----

;; Calling Web Server API
(defn call-api-register-room
  "Post request to register a room in the database"
  [room]
  (let [{:keys [id name]} room]
    (println (str "id: " id " name: " name))
    (client/post "http://localhost:8000/rooms/register"
                         {:body (generate-string {:uuid id
                                                  :name name})
                          :content-type :json})))

(defn call-api-register-sensor
  "Post request to register a room in the database"
  [sensor]
  (let [{:keys [id type room-id status]} sensor]
    (safe-println (str "Sensor: " id " Type: " type " In: " room-id))
    (client/post "http://localhost:8000/sensors/register"
                         {:body (generate-string {:uuid id
                                                  :type type
                                                  :room-id room-id
                                                  :status 1})
                          :content-type :json})))


(defn call-api-update-sensor
  "Post request to update a sensor's status database"
  [id timestamp new-status]
  (client/post "http://localhost:8000/sensors/update"
               {:body (generate-string {:uuid id
                                        :timestamp timestamp
                                        :status new-status})
                :content-type :json}))

;; ---- Run Simulation ----

;; Need to make status update to follow specification. i.e. door should be "closed" or "open"
(defn start-sensor [sensor]
  (go-loop [seconds (rand-interval (:interval sensor))]
    (when (pos? (:status sensor))
      (println (str "Sensor: " (:id sensor) " will wait " seconds " seconds!"))
      (<! (timeout (* seconds 1000)))
      (let [timestamp (now)
            sensor-id (:id sensor)]
        (println (str "Sensor: " sensor-id " logging event at:" timestamp))
        (call-api-update-sensor sensor-id timestamp (rand-int 20)))
      (recur (rand-interval (:interval sensor))))))

(defn run []
  (println "Running Sensors")
  (doseq [sensor @sensor-list]
    (start-sensor sensor))
  (chan))

(defn user-inputs
  []
  (case (read-line)
    "a" (do (println "a command") (recur))
    "b" (do (println "a command") (recur))
    "quit" (do (println "Quitting Simulation") (System/exit 0))
    (do (println "invalid command") (recur))))

(defn -main
  []
  (safe-println "Starting Sensor Simulation ... ")
  (println "Available Commands are as follows:")
  (println "a : a command")
  (println "b : b command")
  (println "quit : quit simulation ")

  ;; ---- Generate rooms and senso
  (update-room-list (make-5-rooms))
  (update-sensor-list (make-n-random-sensors 100))

  ;; ----- Send Room-list and Sensor-list to be created over HTTP
  (doseq [room @room-list]
    (call-api-register-room room))
  (doseq [sensor @sensor-list]
    (call-api-register-sensor sensor))

  ;; --- start the simulation
  (run)

  ;; Loop for accepting user inputs
  (user-inputs))

;; (-main)
