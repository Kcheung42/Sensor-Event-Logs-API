(ns web-server-restful.handler
  (:use compojure.core
        ring.middleware.json
        clojure.core)

  (:require [compojure.handler :as handler]
            [ring.util.response :refer [response]]
            [compojure.route :as route]))

;; ;; defn- is like def but non-public def
;; (defn- str-to [num]
;;   (apply str (interpose ", " (range 1 (inc num)))))

;; (defn- str-from [num]
;;   (apply str (interpose ", " (reverse (range 1 (inc num))))))

(defroutes app-routes
  (route/not-found
   (response {:message "Page not found"})))

;; Just a custom wrapper to test
(defn wrap-log-request [handler]
  (fn [req]
    (println req)
    (handler req)))

;; Application handler
(def app
  (-> app-routes
      wrap-log-request
      wrap-json-response
      wrap-json-body))
