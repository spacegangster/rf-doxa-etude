(ns server.doxa-gist-2
  "Adapted from"
  (:require [ribelo.doxa :as dx]))

(def data [{:db/id 1 :name "Petr" :aka ["Devil"]}])
(def db (dx/create-dx data))

(dx/commit db [[:dx/put {:db/id 1 :name "David" :aka ["Devil"]}]])
(dx/commit db [[:dx/put {:db/id 3 :name "David3" :aka ["Devil"]}]])

(dx/commit {} [[:dx/put {:db/id 1 :name "David" :aka ["Devil"]}]])

