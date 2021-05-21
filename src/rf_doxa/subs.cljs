(ns rf-doxa.subs
  (:require
    [re-frame.core :as rf]
    [ribelo.doxa :as dx]
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


(rf/reg-sub
  :subs.db.todos/filtered
  (fn [db]
    (let [search-text (:db/search-string db "")
          search-re (re-pattern search-text)
          tab (:db/active-tab db :nav.tab/all)
          status (case tab, :nav.tab/due false, :nav.tab/complete true, nil)
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
                   [?e :m/status ?status]
                   [(= ?q-status ?status)]
                   [(re-find ?pat ?g)]]
                  db search-re status))
          ids (flatten query-res)
          idents (mapv #(vector :db/id %) ids)]
      (dx/pull db task-pull-vector idents))))

(comment
  (deref (rf/subscribe [:subs.db.todos/filtered])))

(comment
  (rf/reg-sub
    :subs.db.todos/all
    (fn [db]
      (let [query-res (dx/q [:find ?e :where [?e :m/gist]] db)
            ids (flatten query-res)
            idents (mapv #(vector :db/id %) ids)]
        (dx/pull db task-pull-vector idents)))))

(comment
  (deref (rf/subscribe [:subs.db.todos/all]))
  (dx/q [:find  [?e]
         :where [?e :gist]]
        @re-frame.db/app-db)
  (dx/q [:find  [('pull [:gist] [?e]) ...]
         :where [?e :gist]]
        @re-frame.db/app-db))


(comment
  ;; or you can use `reg-sub-raw` with `dx/with-dx`
  (rf/reg-sub-raw
    :sub.raw.dx/tasks
    (fn [_ [_ foo]]
      (reagent.ratom/reaction
        (dx/with-dx [dx_ :dx.db/meta-or-smth] ; use other dbs here
          (dx/pull @dx_ [:bar] [:db/id foo]))))))
