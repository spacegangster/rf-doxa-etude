(ns rf-doxa.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]
   [rf-doxa.events :as events]))


(def routes
  (atom
    ["/" {""         :nav.tab/all
          "due"      :nav.tab/due
          "complete" :nav.tab/complete}]))


(defn parse
  [url]
  (prn ::parsing url)
  (bidi/match-route @routes url))

(comment
  (parse "/complete"))


(defn url-for
  [nav-map]
  (apply bidi/path-for [@routes (:nav/tab nav-map)]))

(comment
  (url-for {:nav/tab :nav.tab/all}))


(defn dispatch
  [route]
  (prn ::route route)
  (re-frame/dispatch [:evt.db/set-active-tab (:handler route)]))

(def history
  (pushy/pushy dispatch parse))

(defn navigate!
  [nav-map]
  (prn ::nav nav-map)
  (pushy/set-token! history (url-for nav-map)))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :fx/navigate
  (fn [nav-map]
    (navigate! nav-map)))
