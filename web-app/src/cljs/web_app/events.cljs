(ns web-app.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :refer [GET POST]]
   [web-app.db :as db]
   ))

(re-frame/reg-event-db
 :proccess-sensor-response
 (fn
   [db [_ response]]
   (let [parsed-response (.parse js/JSON response)]
     (-> db
         (assoc :loading? false)
         (update-in [:sensor] concat (js->clj parsed-response))))))

(re-frame/reg-event-db
 :proccess-event-response
 (fn
   [db [_ response]]
   (let [parsed-response (.parse js/JSON response)]
     (-> db
         (assoc :events (js->clj parsed-response))))))

(re-frame/reg-event-db
 :bad-response
 (fn
   [db [_ response]]
   (-> db
       (assoc :response (str response)))))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (do
     (GET
      "http://localhost:8000/sensors"
      {:handler       #(re-frame/dispatch [:proccess-sensor-response %1])
       :error-handler #(re-frame/dispatch [:bad-response %1])})
     (GET
      "http://localhost:8000/events"
      {:handler       #(re-frame/dispatch [:proccess-event-response %1])
       :error-handler #(re-frame/dispatch [:bad-response %1])}))
   db/default-db))
