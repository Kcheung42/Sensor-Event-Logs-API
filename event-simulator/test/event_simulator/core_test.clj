(ns event-simulator.core-test
  (:require [clojure.test :refer :all]
            [event-simulator.core :refer :all]
            [expectations.clojure.test :refer :all]))

;; (defexpect make-one-sensor
;;   (with-redefs [sensor-map (atom {})]
;;     (let [id (uuid)]
;;       (expect {:id id :type :motion :room-id "1" :status 1}
;;               (do
;;                 (register-sensor id :motion "1" 1)
;;                 (get-sensor id))))))
