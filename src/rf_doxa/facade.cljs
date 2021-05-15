(ns rf-doxa.facade
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [rf-doxa.doxa-rf-gist]
   [rf-doxa.events :as events]
   [rf-doxa.routes :as routes]
   [rf-doxa.views :as views]
   [rf-doxa.config :as config]))



(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/start!)
  (rf/dispatch-sync [:evt.sys/init-db])
  (dev-setup)
  (mount-root))
