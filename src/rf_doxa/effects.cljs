(ns rf-doxa.effects
  (:require [ribelo.doxa :as dx]
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


(rf/reg-fx
  :fx.dx/register-db
  (fn [[db-name get-db-atom]]
    (dx/reg-dx! db-name (get-db-atom))))

(rf/reg-fx
  :fx.dx/put
  commit!)
