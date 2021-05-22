(ns rf-doxa.subs
  (:require
    [re-frame.core :as rf]
    [ribelo.doxa :as dx]
    [meander.epsilon :as m]
    [space.matterandvoid.malli-gen.api :as mcg]
    [rf-doxa.specs]))


(rf/reg-sub
  :subs.db/active-tab
  (fn [db _]
    (:db/active-tab db)))


(rf/reg-sub
  :subs.db/search-string
  (fn [db] (:db/search-string db "")))

(def task-pull-vector
  (mcg/schema->eql rf-doxa.specs/schema:task {}))


(defn calc-tasks [db search-string status]
  (let [search-re (re-pattern search-string)
        query-res
        (if (nil? status)
          (dx/q [:find ?e
                 :in ?pat
                 :where
                 [?e :m/gist ?g]
                 [(re-find ?pat ?g)]]
                db search-re)
          (dx/q [:find ?e
                 :in ?pat ?q-status
                 :where
                 [?e :m/gist ?g]
                 [?e :m/status ?q-status]
                 [(re-find ?pat ?g)]]
                db search-re status))
        ids (mapv first query-res)
        idents (mapv #(vector :db/id %) ids)]
    (dx/pull db task-pull-vector idents)))

(comment
  (calc-tasks
    (dx/create-dx [{:db/id 1 :m/gist "some"} {:db/id 2 :m/status true :m/gist "some"}])
    "" nil))


(rf/reg-sub
  :subs.db.todos/filtered
  (fn [db]
    (let [search-text (:db/search-string db "")
          tab (:db/active-tab db :nav.tab/all)
          status (case tab, :nav.tab/due false, :nav.tab/complete true, nil)]
      (calc-tasks db search-text status))))


(comment
  (deref (rf/subscribe [:subs.db.todos/filtered]))

  (let [db (dx/create-dx [{:db/id 1 :m/gist "some"}
                          {:db/id 2 :m/status true :m/gist "some"}])]
    (dx/q [:find ?e
           :in ?q-status
           :where
           [?e :m/status (if (some? ?q-status) ?q-status (m/any ?status))]]
          db nil))

  (let [db (deref re-frame.db/app-db)
        db (dx/create-dx [{:db/id 1 :m/gist "some"}] [{:db/id 2 :m/status true :m/gist "some"}])]
    (dx/q [:find ?e
           :in ?pat ?q-status
           :where
           [?e :m/gist ?g]
           [?e :m/status (m/any ?status)]
           [(re-find ?pat ?g)]]
          db #"" nil)))


(comment
  (rf/reg-sub
    :subs.db.todos/all
    (fn [db]
      (let [query-res (dx/q [:find ?e :where [?e :m/gist]] db)
            ids (flatten query-res)
            idents (mapv #(vector :db/id %) ids)]
        (dx/pull db task-pull-vector idents))))

  ; may be fixed in the near future
  (let [dx1 (dx/create-dx [{:db/id 1 :m/gist "buy milk 1", :m/status false}
                           {:db/id 4 :m/gist "buy milk 4", :m/status false}])]
    [(dx/q [:find [(pull [:*] [?table ?e]) ...]
            :where [[?table ?e] :m/gist]]
           dx1)
     (dx/q [:find [(pull [:*] [?table ?e]) ...]
            :where [?table ?e :m/gist]]
           dx1)])

  (dx/q [:find ?e :where [?e :m/gist]] @re-frame.db/app-db))


(comment
  (deref (rf/subscribe [:subs.db.todos/all]))
  (dx/q [:find  ?e
         :where [?e :m/gist]]
        @re-frame.db/app-db)
  (dx/q [:find  [('pull [:m/gist] [?e]) ...]
         :where [?e :m/gist]]
        @re-frame.db/app-db))


(comment
  ;; or you can use `reg-sub-raw` with `dx/with-dx`
  (rf/reg-sub-raw
    :sub.raw.dx/tasks
    (fn [_ [_ foo]]
      (reagent.ratom/reaction
        (dx/with-dx [dx_ :dx.db/meta-or-smth] ; use other dbs here
          (dx/pull @dx_ [:bar] [:db/id foo]))))))
