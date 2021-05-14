(ns rf-doxa.doxa-gist-2
  "Adapted from"
  (:require [ribelo.doxa :as dx]
            [re-frame.core :as rf]))

(def data [{:db/id 1 :name "Petr" :aka ["Devil"]}])
(def db (dx/create-dx data))
(dx/commit db [[:dx/put {:db/id 1 :name "David" :aka ["Devil"]}]])

