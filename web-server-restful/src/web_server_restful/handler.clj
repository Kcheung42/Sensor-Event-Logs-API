(ns web-server-restful.handler
  (:use compojure.core
        ring.middleware.json
        ring.middleware.keyword-params
        ring.middleware.params
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
                                             register-room
                                             get-all-rooms
                                             update-sensor-status]]
            [clojure.core.async
             :as a
             :refer [>! <! go chan]]
            ))


;; ---- Channels -----

;; Setting up Channels for asychronous handling of Post Requests
;; Post requests will put! onto the channel
(def q-chan (chan))

;; ---- API handlers -----

(defn app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain;=us-ascii"}
   :body (str request)})

(defn handle-register-sensor [uuid type room-id status]
  (go (>! q-chan (register-sensor uuid type room-id status)))
  (response "Sensor Registered Successfully")) ;; should actually read response to determine success

(defn handle-update-sensor [uuid timestamp new-status]
  (go (>! q-chan (update-sensor-status uuid timestamp new-status)))
  (response "Update Successful")) ;; should actually read response to determine success

;; --- Getters

(defn handle-get-all-sensors []
  {:status 200
   :headers {"Content-Type" "application-json"
             "Access-Control-Allow-Origin" "*"
             "Access-Control-Allow-Headers" "Content-Type"}
   :body (get-all-sensors)})

(defn handle-get-sensor [id]
  {:status 200
   :headers {"Content-Type" "application-json"}
   :body @(get-sensor-atom id)})
;; (response @(get-sensor-atom id)) ;; same as above

(defn handle-get-all-rooms []
  {:status 200
   :headers {"Content-Type" "application-json"
             "Access-Control-Allow-Origin" "*"
             "Access-Control-Allow-Headers" "Content-Type"}
   :body (get-all-rooms)})

(defn handle-create-room [uuid name]
  (go (>! q-chan (register-room uuid name)))
  (response "Room Registered Successfully"))


(defn handle-get-all-events []
  {:status 200
   :headers {"Content-Type" "application-json"
             "Access-Control-Allow-Origin" "*"
             "Access-Control-Allow-Headers" "Content-Type"}
   :body (get-all-events)})

;; ----- Routes -----

(defroutes app-routes
  (context "/sensors" []
           (defroutes sensor-routes
             (GET "/" [] (handle-get-all-sensors))
             (GET "/:uuid" [uuid] (handle-get-sensor uuid))
             (POST "/register"
                   {{:strs [uuid type room-id status]} :body} ;; destructuring the request
                   (handle-register-sensor uuid type room-id status))
             (POST "/update"
                   {{:strs [uuid timestamp status]} :body} ;; destructuring the request
                   (handle-update-sensor uuid timestamp status))
             ))

  ;; Subroutes for localhost:8000/rooms/
  (context "/rooms" []
           (defroutes room-routes
             (GET "/" [] (handle-get-all-rooms))
             ;;TODO (GET "/uuid" [uuid] (handle-get-room uuid))
             (POST "/register"
                   {{:strs [uuid name]} :body} ; must use :str instead of :keys
                   (handle-create-room uuid name))))

  ;; Subroutes for localhost:8000/events/
  (context "/events" []
           (defroutes events
             (GET "/" [] (handle-get-all-events))))

  (GET "/" [] "Welcome to Starcity")
  (GET "/request" request (app-handler request)) ;; TODO remove
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
      wrap-json-body
      wrap-keyword-params
      wrap-params))


;; ----- start server ----
(defn -main
  [port]
  (go (while true (println (<! q-chan))))
  (jetty/run-jetty app {:port (Integer. port)}))
