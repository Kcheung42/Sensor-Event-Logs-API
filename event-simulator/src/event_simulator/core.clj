(ns event-simulator.core
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]))

;; ## Part 1: Sensor Event Simulation

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
  {"motion" [1 10]
   "light" [10 30]
   "door" [60 120]})

;; --- Helper functions

(defn get_date []
  (java.util.Date))

(defn uuid
  "Generate unique id"
  [] (str (java.util.UUID/randomUUID)))

(defn get-random-room-id []
  (:id (rand-nth @room-list)))


(defn get-random-sensor-type []
  (rand-nth (vec sensor-types)))


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

;; ---- Run Simulation ----



;; -----

;; Calling Web Server API
(defn call-api-register-room
  "Post request to register a room in the database"
  [room]
  (let [{:keys [id name]} room]
    (println (str "id: " id " name: " name))
    (future (client/post "http://localhost:8000/rooms/register"
                         {:body (generate-string {:uuid id
                                                  :name name})}))))

(defn -main
  []
  (update-room-list (make-5-rooms))
  (update-sensor-list (make-n-random-sensors 5))
  ;; TODO
  ;; ----- Send Room-list and Sensor-list to be created over HTTP

  ;; Creates one room
  (client/post "http://localhost:8000/rooms/register"

               {:body (generate-string {:uuid "1"
                                        :name "living"})
                :content-type :json})

  (map call-api-register-room @room-list)
  )

;; (-main)


;;--- testing http requests -----

;; (client/get "http://localhost:8000/sensors")

;; Registering one room

;; Registering one sensor
;; (client/post "http://localhost:8000/sensors/register"
;;              {:body (generate-string {:uuid "1"
;;                                       :type "living"
;;                                       :room-id "1"
;;                                       :status "1"})
;;               :content-type :json})
