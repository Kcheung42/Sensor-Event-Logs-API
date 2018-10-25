(ns web-app.views
  (:require
   [re-frame.core :as re-frame]
   [web-app.subs :as subs]
   ))

;; (defn request-it-button []
;;   [:div {:class "button-class"
;; :on-click #(dispatch [:request-it])}
;;       :on-click #(println "hello World")}
;; "I wan't it now"])

(defn db-initialized? []
  (let [loading? (re-frame/subscribe [::subs/loading?])]
    [:div
     (if @loading?
       "Loading db ... "
       "Loading Success!")]))

(defn sensor-list []
  (let [sensors (re-frame/subscribe [::subs/sensors])]
    [:ul
     (for [sensor @sensors]
       ^{:key (:id sensor)} [:li "Sensor:" sensor])]))

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
      [sensor-list]
      [event-log]
      ]
     ;; [request-it-button]
     ]))
