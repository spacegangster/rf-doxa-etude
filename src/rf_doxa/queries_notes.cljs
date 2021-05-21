(ns rf-doxa.queries-notes
  (:require
    [re-frame.core :as rf]
    [ribelo.doxa :as dx]))



(comment
  (let [db (dx/create-dx [{:db/id 1 :m/gist "ggg"} {:db/id 2 :m/status false :m/gist "ggg"}])
        q-status nil
        cond1 (if (nil? q-status)
                ['?e :db/id]
                ['?e :m/status '?status])]
    (dx/q [:find ?e
           :in ?pat ?q-status
           :where
           [?e :m/gist ?g]
           cond1
           [(re-find ?pat ?g)]]
          db #"" q-status)))


(comment
  (let [db (dx/create-dx [{:db/id 1 :m/gist "ggg"} {:db/id 2 :m/status false :m/gist "ggg"}])
        q-status #{nil false true}
        matches-status (fn [%] ; % is a number
                         (prn %)
                         (contains? q-status (:m/status %)))]
    [(dx/q [:find ?e
            :in ?pat ?q-status
            :where
            [?e :m/gist ?g]
            [?e :m/status ?status]
            [(re-find ?pat ?g)]
            [(contains? ?q-status ?status)]]
           db #"" q-status)

     (dx/q [:find ?e
            :in ?pat ?q-status
            :where
            [?e :m/gist ?g]
            [?e :m/status ?s]
            [(re-find ?pat ?g)]
            [(matches-status ?e)]]
           db #"" q-status)]))


