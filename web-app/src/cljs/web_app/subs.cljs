(ns web-app.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (:loading? db)))

(re-frame/reg-sub
 ::response
 (fn [db]
   (:response db)))


(re-frame/reg-sub
 ::sensors
 (fn [db]
   (:sensor db)))

(re-frame/reg-sub
 ::events
 (fn [db]
   (:events db)))

(re-frame/reg-sub
 ::rooms
 (fn [db]
   (:rooms db)))
