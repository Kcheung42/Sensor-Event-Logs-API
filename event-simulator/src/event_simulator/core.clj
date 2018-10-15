(ns event-simulator.core
  (:require [clj-http.client :as client]))

;; ## Part 1: Sensor Event Simulation

;; ---- Constants and Storage-----

;; Atoms to store list of entities

;; store a map where
;; key: is the same as uuid,
;; value: as the entitiy map
(def room-list (atom {}))
(def sensor-list (atom {}))

(def room-types
  ["living" "media" "bathroom-1" "bathroom-2" "front-door" "back-door"])

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

(defn get-random-room []
  (rand-nth (keys @room-list)))

(defn get-random-sensor-type []
  (rand-nth (keys sensor-types)))

;; --- Create Sensors and Rooms ----

(defn make-one-room [room]
  (let [id (uuid)]
    {(keyword id) {:id id
                   :name room}}))

(defn make-5-rooms []
  (reduce (fn [result room]
            (concat result (make-one-room room)))
          []
          room-types))

;; Sample Output
(make-5-rooms)
#_([:c3ba3fc1-4bbb-471c-b085-adb1fecedbfe
    {:id "c3ba3fc1-4bbb-471c-b085-adb1fecedbfe", :name "living"}]
   [:aa1041e5-7fa5-4d24-b57a-abd968de2292
    {:id "aa1041e5-7fa5-4d24-b57a-abd968de2292", :name "media"}]
   [:b15e74be-1f18-47b2-b8b9-e23f9fa0328a
    {:id "b15e74be-1f18-47b2-b8b9-e23f9fa0328a", :name "bathroom-1"}]
   [:294e9c91-63ba-4a01-bcc7-6809ab9243e0
    {:id "294e9c91-63ba-4a01-bcc7-6809ab9243e0", :name "bathroom-2"}]
   [:7ecd65c7-c6c1-4c97-9b9a-586623a28ae5
    {:id "7ecd65c7-c6c1-4c97-9b9a-586623a28ae5", :name "front-door"}]
   [:4a0f53d7-b634-483d-becd-195c0dd367b9
    {:id "4a0f53d7-b634-483d-becd-195c0dd367b9", :name "back-door"}])

(defn make-one-sensor [sensor-type room-id]
  (let [id (uuid)
        [type interval] sensor-type]
    {(keyword id) {:id id
                   :type type
                   :room-id room-id
                   :status 1
                   :interval interval}}))

(defn make-n-random-sensors [n]
  (reduce (fn [result type]
               (concat result (make-one-sensor type (get-random-room))))
             []
             (repeatedly n #(rand-nth (vec sensor-types)))))

;; Sample Output
(make-n-random-sensors 3)
#_([:b8d34866-2533-4b66-a319-b7f1e80752d6
  {:id "b8d34866-2533-4b66-a319-b7f1e80752d6",
   :type "motion",
   :room-id :dcd83401-36ff-496f-b550-411c4b1c3aee,
   :status 1,
   :interval [1 10]}]
 [:57e2643a-9415-448e-af0c-ca20f92cbc6a
  {:id "57e2643a-9415-448e-af0c-ca20f92cbc6a",
   :type "door",
   :room-id :bc2136e5-8559-4cbb-935e-dd4108425a23,
   :status 1,
   :interval [60 120]}]
 [:f21fa77c-25fd-48ea-8d92-1104879c2120
  {:id "f21fa77c-25fd-48ea-8d92-1104879c2120",
   :type "light",
   :room-id :651ea140-d5cc-42bf-b3d6-5b0b96759097,
   :status 1,
   :interval [10 30]}])

;; ---- Update list functions ---

(defn update-room-list [list-of-rooms]
  (swap! room-list conj list-of-rooms))

(defn update-sensor-list [list-of-sensors]
  (swap! sensor-list conj list-of-sensors))

(defn -main
  []
  (update-room-list (make-5-rooms))
  (update-sensor-list (make-n-random-sensors 5))
  (println "Below is your list of room-id")
  (identity (let [rooms @room-list]
              (zipmap (keys rooms)
                      (map (fn[[key value]](:name value)) rooms))))
  (println "Below is your list of sensor-id")
  (identity (let [sensor @sensor-list]
              (zipmap (keys sensor)
                      (map (fn[[key value]](:type value)) @sensor-list))))
  )


(-main)

;; (zipmap (keys @room-list) (map :type ))
;; (println @room-list))
;; (println (str "this is your list of sensors" (@sensor-list))))

;; ----- Send Room-list and Sensor-list to be created
;; ----- in web server

;; (update-room-list (make-5-rooms 5))
;; (update-sensor-list (make-n-random-sensors 5))
;; (identity @room-list)
;; (identity @sensor-list)

;; (make-n-random-sensors 10)

;;--- testing http requests -----

;; (client/get "http://localhost:8000/sensors")
;; (client/post "http://localhost:8000/register"
;;              {:form-params {:uuid "1"
;;                             :type "door"
;;                             :room-id "1"
;;                             :status "1"}
;;               :content-type :json})
