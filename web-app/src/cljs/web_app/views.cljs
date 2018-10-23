(ns web-app.views
  (:require
   [re-frame.core :as re-frame]
   [web-app.subs :as subs]
   ))

(defn request-it-button []
  [:div {:class "button-class"
         ;; :on-click #(dispatch [:request-it])}
         :on-click #(println "hello World")}
   "I wan't it now"])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [request-it-button]
     ]))
