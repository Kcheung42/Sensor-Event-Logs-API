(ns web-server-restful.handler
  (:use compojure.core
        ring.middleware.json
        clojure.core)

  (:require [compojure.handler :as handler]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :as jetty]
            [compojure.route :as route]
            [web-server-restful.core :refer [get-all-sensors
                                             get-all-events
                                             get-sensor-atom
                                             get-event
                                             register-sensor
                                             update-sensor-status]]))

;; ;; defn- is like def but non-public def
;; (defn- str-to [num]
;;   (apply str (interpose ", " (range 1 (inc num)))))

;; (defn- str-from [num]
;;   (apply str (interpose ", " (reverse (range 1 (inc num))))))

;; ---- API handlers -----

(defn app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain;=us-ascii"}
   :body (str request)})

(defn handle-register-sensor [id type room-id status]
  (response
   (register-sensor id type room-id status)))

(defn handle-get-all-sensors []
  {:status 200
   :headers {"Content-Type" "application-json"}
   :body (get-all-sensors)})

(defn handle-get-sensor [id]
  (response @(get-sensor-atom id)))

(defn handle-get-all-rooms []
  (response ("TODO: here is your room")))



;; ----- Routes -----

(defroutes app-routes
  (context "/sensors" []
           (defroutes sensor-routes
             (GET "/" [] (handle-get-all-sensors))
             (GET "/:uuid" [uuid] (handle-get-sensor uuid))
             (POST "/register"
                   {{:keys [uuid type room-id status]} :body}
                   (handle-register-sensor uuid type room-id status))))

  (context "/rooms" []
           (defroutes room-routes
             (GET "/" [] (handle-get-all-rooms))))

  ;; (context "/events" []
  ;;          (defroutes sensor-routes
  ;;            (GET "/" [] (get-all-sensors))

  (GET "/" [] "Welcome to Starcity")
  (GET "/request" request (app-handler request))
  (route/not-found
   (response {:message "Page not found"})))

;; ---- Wrapers -----

;; Just a custom wrapper to test
(defn wrap-log-request [handler]
  (fn [req]
    (println req)
    (handler req)))

;; Application
(def app
  (-> (handler/api app-routes)
      wrap-log-request
      wrap-json-response
      wrap-json-body))

;; ----- start server ----
(defn -main
  [port]
  (jetty/run-jetty app {:port (Integer. port)}))

;; curl -X PUT -H "Content-Type: application/json" \
;; -d '{"attrs": {"tag": "foo"}}' \
;; {"uuid" : "1"
;;  "type" : "door"
;;  ""}

;; http://localhost:8000/register/1
