(ns rf-doxa.db)

(def default-db
  {:name "re-frame"})

(def default-tasks
  [{:db/id 1 :m/gist "buy milk 1"}
   {:db/id 2 :m/gist "buy milk 2"}
   {:db/id 3 :m/gist "buy milk 3"}
   {:db/id 4 :m/gist "buy milk 4"}])

(defn default-tasks-transactions []
  (mapv #(vector :dx/put %) default-tasks))