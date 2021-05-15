(ns rf-doxa.subs
  (:require
    [re-frame.core :as rf]
    [ribelo.doxa :as dx]))

(rf/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(def task-pull-vector
  [:m/gist :db/id])

(rf/reg-sub
  :subs.db.todos/all
  (fn [db]
    (dx/q [:find [(pull [:*] [?table ?e]) ...] :where [[?table ?e] :m/gist]] db)))


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
