(ns rf-doxa.doxa-gist-2
  "Adapted from"
  (:require [ribelo.doxa :as dx]
            [re-frame.core :as rf]))

(def data [{:db/id 1 :name "Petr" :aka ["Devil"]}])
(defonce db (atom (dx/create-dx data)))
(dx/commit! db [[:dx/put {:db/id 2 :name "David2" :aka ["Devil"]}]])
(dx/commit! db [[:dx/put {:db/id 3 :name "David3" :aka ["Devil"]}]])

(dx/pull @db [:name] [:db/id 2])
(dx/pull @db {[:db/id 2] [:name]})

(dx/q
  [:find ?e ; be sure to start symbols with ?, otherwise won't work
   :where [?e :name]]
  @db)

