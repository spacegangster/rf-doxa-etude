(ns rf-doxa.specs
  (:require [malli.core :as m]))

(def schema:task
  [:schema
   {:registry
    {:m/gist   string?
     :m/status boolean?
     :db/id    int?
     :m/task   [:map
                [:m/gist {:default ""}]
                [:m/status {:default false}]
                :db/id]}}
   :m/task])

(comment
  (malli.core/schema schema:task))