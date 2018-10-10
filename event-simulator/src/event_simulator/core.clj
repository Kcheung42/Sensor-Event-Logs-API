(ns event-simulator.core)

;; ---- Helper functions -----
(defn get_date []
  (java.util.Date))

(defn uuid
  "Generate unique id"
  [] (str (java.util.UUID/randomUUID)))

;; ----- Database/identities----

(def room-list (atom [{:test "This is a test"}]))
(def sensor-list (atom []))

(def rooms
  [:living :media :bathroom-1 :bathroom-2])

;; (def room
  ;; "example room"
  ;; {:id "1" :name "living"})

(defn make-one-room [room]
  {:id (uuid)
   :name room})

(defn generate-rooms [n]
  (reduce (fn[result room]
            (conj result (make-one-room room)))
          []
          (take n (repeat (rand-nth rooms)))))

(take 1 (repeat (rand-nth rooms)))

(generate-rooms 10)

(defn update-room-list [list-of-rooms]
  (swap! room-list concat list-of-rooms))

(update-room-list (generate-rooms 10))

(println @room-list)

(def sensor
  "example sensor"
  {:id "1" :type :motion :room-id "1" :status 1})


(def event
  "example room"
  {:id "1" :timestamp get_date :sensor-id "1"})
