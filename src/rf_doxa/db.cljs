(ns rf-doxa.db)

(def default-db
  {:name "re-frame-doxa"})

(def default-tasks
  [{:db/id 1 :m/gist "buy milk 1", :m/status false}
   {:db/id 2 :m/gist "buy milk 2", :m/status false}
   {:db/id 3 :m/gist "buy milk 3", :m/status false}
   {:db/id 4 :m/gist "buy milk 4", :m/status false}])

(defn default-tasks-transactions []
  (mapv #(vector :dx/put %) default-tasks))