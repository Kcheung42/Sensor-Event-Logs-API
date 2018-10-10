(ns web-server-restful.core-test
  (:require [web-server-restful.core :refer :all]
            [ring.mock.request :as mock]
            [web-server-restful.handler :as handler]
            [expectations.clojure.test :refer :all]))

(defexpect creating-sensor
  (with-redefs [sensor-list (atom [])]
    (let [sensor {:id "1", :type :motion, :room-id "1", :status 1}]
      (expect sensor
              (do
                (create-sensor sensor)
                (first (get-all-sensors)))))))


(defexpect createing-multiple-sensors
  (with-redefs [sensor-list (atom [])]
    (expect 3
            (do
              (create-sensor
               {:id "1", :type :motion, :room-id "1", :status 1})
              (create-sensor
               {:id "2", :type :motion, :room-id "1" :status 1})
              (create-sensor
               {:id "3", :type :motion, :room-id "1" :status 1})
              (get-atom-list-count sensor-list)))))

(defexpect creating-invalid-sensor-nounique
  (with-redefs [sensor-list (atom [])]
    (expect 1
            (do
              (create-sensor
               {:id "1", :type :motion, :room-id "living" :status 1})
              (create-sensor
               {:id "1", :type :motion, :room-id "living" :status 1})
              (get-atom-list-count sensor-list)))))

;; (defexpect creating-event
;;   (with-redefs [event-log (atom [])]
;;     (let [timestamp (get_date)]
;;       (expect 1
;;               (do
;;                 (create-event {:id "1" :time timestamp :sensor-id "1"})
;;                 (get-atom-list-count event-log))))))

(defexpect validate-sensor
  (expecting "a valid sensor has an id, type, room-id it is in, and status"
             (expect false (valid-sensor? {:type :motion, :room-id "1"}))
             (expect false (valid-sensor? {:id "1", :room-id "1"}))
             (expect false (valid-sensor? {:id "1", :type :motion}))
             (expect false (valid-sensor? {:wrong-keys "1"}))))

;; (defexpect creating-event
;;   (with-redefs [event-log (atom [])]
;;     (expect 1
;;             (do
;;               (create-event {:id "1" :type "motion"})))))


;; (defexpect sensor-endpoint
;;   (let [response (handler/app (mock/request :get "/sensors"))]
;;     (expect 200
;;             ((:status response)))
;;     (expect "application-json"
;;             (get-in response [:headers "ContentType"]))))

;; (defexpect events-endpoint
;;   (let [response (handler/app (mock/request :get "/events"))]
;;     (expect 200
;;             ((:status response)))
;;     (expect "application-json"
;;             (get-in response [:headers "ContentType"]))))

;; (defexpect not-found-route
;;   (let [response (handler/app (mock/request :get "/bogus-route"))]
;;     (expect 404
;;             ((:status response)))))
