(ns web-server-restful.core-test
  (:require [web-server-restful.core :refer :all]
            [ring.mock.request :as mock]
            [web-server-restful.handler :as handler]
            [expectations.clojure.test :refer :all]))

;; Opted to use with-redefs as opposed to dynamic var binding.

;; ----- Rooms -----

(defexpect create-room
  (with-redefs [room-map (atom {})]
    (let [id (uuid)
          name "living"]
      (expect {:id id :name name}
              (do
                (register-room id name)
                (get-room id))))))

;; ----- Sensors -----
;; Progblem with test. Date not as expected.
;; How can I freeze the date when creating an event so it can match
;; expectations? or How to ignore a certain part of the output?
(defexpect register-one-sensor
  (with-redefs [sensor-map (atom {})
                room-map (atom {})]
    (let [id (uuid)
          date (now)]
      (expect {:id id :type :motion :room-id "1" :status 1 :last-updated date}
              (do
                (register-room "1" "living")
                (register-sensor id :motion "1" 1))))))

(defexpect register-multiple-sensors
  (with-redefs [sensor-map (atom {})
                room-map (atom {})]
    (expect 3
            (let [room-id "1"]
              (do
                (register-room room-id "living")
                (register-sensor "1" :motion room-id 1)
                (register-sensor "2" :motion room-id 1)
                (register-sensor "3" :motion room-id 1)
                (get-atom-list-count sensor-map))))))

(defexpect creating-invalid-sensor-nounique
  (with-redefs [sensor-map (atom {})
                room-map (atom {})]
    (expect 1
            (let [room-id "1"]
              (do
                (register-room room-id "living")
                (register-sensor "1" :motion room-id 1)
                (register-sensor "1" :motion room-id 1)
                (get-atom-list-count sensor-map))))))

(defexpect creating-invalid-sensor-room-not-exist
  (with-redefs [sensor-map (atom {})
                room-map (atom {})]
    (expect 0
            (do
              (register-room "1" "living")
              (register-sensor "id-1" :motion "no-room-id" 1)
              (get-atom-list-count sensor-map)))))



;; ----- Events -----

(defexpect creating-event
  (with-redefs [event-log (atom {})
                sensor-map (atom {})]
    (let [date (now)
          room-id "room-id"
          sensor-id "sensor-1"]
      (expect {:id "event-id", :timestamp date, :sensor-id sensor-id, :status 1}
              (do
                (register-room room-id "living")
                (register-sensor sensor-id :motion room-id 1)
                (log-event "event-id" date sensor-id 1)
                (get-event "event-id"))))))


(defexpect creating-multiple-events
  (with-redefs [event-log (atom {})
                sensor-map (atom {})]
    (expect 3
            (let [date (now)
                  room-id "room-id"
                  sid "sensor-1"]
              (do
                (register-sensor sid :motion room-id 1)
                (log-event "1" date sid 1)
                (log-event "2" date sid 1)
                (log-event "3" date sid 1)
                ;; (get-all-events))))))
                (get-atom-list-count event-log))))))

(defexpect should-create-events-when-sensor-status-change
  (with-redefs [event-log (atom {})
                sensor-map (atom {})
                room-map (atom {})]
    (expect 2
            (let [date (now)
                  room-id "room-id"
                  sid "sensor-1"]
              (do
                (register-room room-id "living")
                (register-sensor sid :motion room-id 1)
                (update-sensor-status sid (now) 101 )
                (update-sensor-status sid (now) 102 )
                (get-atom-list-count event-log))))))

;; ---- Testing API endpionts -----

;; (defexpect sensor-endpoint
;;   (let [response (handler/app (mock/request :get "/sensors"))]
;;     (expect 200
;;             (:status response))
;;     (expect "application-json"
;;         (get-in response [:headers "Content-Type"]))
;; ))

(defexpect post-request-should-create-a-room
  (with-redefs [room-map (atom {})]
    (let [response (handler/app (-> (mock/request :post "/rooms/register")
                                    (mock/json-body {"uuid" "1" "name" "living"})))]
      (expect {:1 {:id "1" :name "living"}}
              (in @room-map)))))

(defexpect post-request-should-create-a-sensor
  (with-redefs [room-map (atom {})
                sensor-map (atom {})]

    (let [room-id "room-1"]
      (do
        (register-room room-id "living")
        (let [response (handler/app (-> (mock/request :post "/sensors/register")
                                        (mock/json-body {"uuid" "1"
                                                         "type" "motion"
                                                         "room-id" room-id
                                                         "status" "1"})))]
          (expect 1
                  (get-atom-list-count sensor-map)))))))


;; Lesson: Destructure using :str instead of Keys!
;; Currently failing because the body is not being destructured properly
;; in  handler.clj Line 74:
;; (POST "/register"
;;       {{:keys [uuid name]} :body} ;; destructuring the request




;; (defexpect events-endpoint
;;   (let [response (handler/app (mock/request :get "/events"))]
;;     (expect 200
;;             ((:status response)))
;;     (expect "application-json"
;;             (get-in response [:headers "ContentType"]))))

(defexpect not-found-route
  (let [response (handler/app (mock/request :get "/bogus-route"))]
    (expect 404
            (:status response))))


;; Learnings:
;; Adding validation code later on breaks more of your tests. Is there a way to mitigate this?
