(ns rf-doxa.effects
  (:require [ribelo.doxa :as dx]
            [rf-doxa.logging :as log]
            [re-frame.core :as rf]))


(defn commit!
  "put-able may be a map {:db/id 1 ...}
  or a vec [[:db/id 1] :prop val]
  or a vec2 [[:db/id 1] {:prop val}]"
  [[put-able & [opts]]]
  (let [db-name (:dx/db-name opts :dx.db/app)
        put-vec (if (map? put-able)
                  [:dx/put put-able]
                  (into [:dx/put] put-able))]
    (dx/with-dx [db db-name]
      (dx/commit! db put-vec))))

(comment
  (commit! [{:db/id 5 :gist "task 5"}])
  re-frame.db/app-db)


(defn on-db-events [db]
  (let [cur-meta (meta db)
        diffs (:tx cur-meta)]
    (prn cur-meta)
    (log/log ::on-db db (pr-str diffs))
    (doseq [[path op-type ?new-val] diffs]
      ; op-type one of :r (replace), :+, :-
      (let [diff-evt (cond-> {:diff/path path, :diff/op-type op-type, :diff/time (:t cur-meta)}
                             (some? ?new-val) (assoc :diff/value ?new-val))]
        (rf/dispatch [:evt.sys/post-diff diff-evt])))))


(rf/reg-fx
  :fx.server/post-diff
  (fn [diff]
    (log/log ::post-diff diff)))

(rf/reg-fx
  :fx.dx/register-db
  (fn [[^keyword db-name, get-db-atom]]
    (let [db-atom (get-db-atom)]
      (dx/reg-dx! db-name db-atom)
      (dx/listen! @db-atom on-db-events))))

(comment
  (meta @re-frame.db/app-db)
  (def a (with-meta {:my :map} {:meta-key1 :one}))
  (meta a)
  (meta (merge a {:two 'two}))
  (meta (merge {:two 'two} a)))


(rf/reg-fx
  :fx.dx/put
  commit!)
