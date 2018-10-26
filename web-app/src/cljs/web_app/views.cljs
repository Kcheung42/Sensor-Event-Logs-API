(ns web-app.views
  (:require
   [re-frame.core :as re-frame]
   [web-app.subs :as subs]
   ))

(defn db-initialized? []
  (let [loading? (re-frame/subscribe [::subs/loading?])]
    [:div
     (if @loading?
       "Loading db ... "
       "Loading Success!")]))

(defn room-list []
  (let [rooms (re-frame/subscribe [::subs/rooms])]
    [:ul
     (for [room @rooms]
       ^{:key (:id room)} [:li "Room: " room])]))


(defn sensor-list []
  (let [sensors (re-frame/subscribe [::subs/sensors])]
    [:ul
     (for [sensor @sensors]
       ^{:key (:id sensor)} [:li "Sensor: " sensor])]))

(defn event-log []
  (let [events (re-frame/subscribe [::subs/events])]
    [:ul
     (map (fn [[k v] e]
            [:li "Log: " v])
          @events)]))

(defn get-response []
  (let [response (re-frame/subscribe [::subs/response])]
    [:div
     @response]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [db-initialized?]
     [get-response]
     [:span
      [:hr]
      [:p "Registered Rooms"]
      [room-list]
      [:hr]
      [:p "Registered Sensors"]
      [sensor-list]
      [:hr]
      [:p "Event Logs"]
      [event-log]]]))
