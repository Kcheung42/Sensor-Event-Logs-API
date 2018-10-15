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
(defexpect creating-sensor
  (with-redefs [sensor-map (atom {})]
    (let [id (uuid)
          date (now)]
      (expect {:id id :type :motion :room-id "1" :status 1 :last-updated date}
              (do
                (register-sensor id :motion "1" 1)
                @(get-sensor-atom id))))))

(defexpect createing-multiple-sensors
  (with-redefs [sensor-map (atom {})]
    (expect 3
            (do
              (register-sensor "1" :motion "1" 1)
              (register-sensor "2" :motion "1" 1)
              (register-sensor "3" :motion "1" 1)
              (get-atom-list-count sensor-map)))))

(defexpect creating-invalid-sensor-nounique
  (with-redefs [sensor-map (atom {})]
    (expect 1
            (do
              (register-sensor "1" :motion "1" 1)
              (register-sensor "1" :motion "1" 1)
              (get-atom-list-count sensor-map)))))





;; ----- Events -----

(defexpect creating-event
  (with-redefs [event-log (atom {})
                sensor-map (atom {})]
    (let [date (now)
          sensor-id "1"]
      (expect {:id "event-id", :timestamp date, :sensor-id sensor-id, :status 1}
              (do
                (register-sensor sensor-id :motion "room-1" 1)
                (log-event "event-id" date sensor-id 1)
                (get-event "event-id"))))))


(defexpect creating-multiple-events
  (with-redefs [event-log (atom {})]
    (expect 3
            (let [date (now)]
              (do
                (register-sensor "sens-1" :motion "1" 1)
                (log-event "1" date "sens-1" 1)
                (log-event "2" date "sens-1" 1)
                (log-event "3" date "sens-1" 1)
                ;; (get-all-events))))))
                (get-atom-list-count event-log))))))

(defexpect update-light-sensor-past-threshold
  (with-redefs [event-log (atom {})
                sensor-map (atom {})]
    (expect 3
            (let [date (now)]
              (do
                (register-sensor "sens-1" :motion "1" 1)
                (log-event "1" date "sens-1" 1)
                (log-event "2" date "sens-1" 1)
                (log-event "3" date "sens-1" 1)
                (get-atom-list-count event-log))))))

;; ---- Testing API endpionts -----

(defexpect sensor-endpoint
  (let [response (handler/app (mock/request :get "/sensors"))]
    (expect 200
            (:status response))
    (expect "application-json"
            (get-in response [:headers "Content-Type"]))
    ))

;; (defexpect post-request-should-create-)

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
