(ns rf-doxa.effects
  (:require [ribelo.doxa :as dx]
            [re-frame.core :as rf]))

(rf/reg-fx
  :fx.dx/register-db
  (fn [[db-name get-db-atom]]
    (dx/reg-dx! db-name (get-db-atom))))

(rf/reg-fx
  :fx.dx/put
  (fn commit!-to-app-db
    [[id-vec data & [opts]]]
    (let [db-name (:dx/db-name opts :dx.db/app)]
      (dx/with-dx [db db-name]
        (dx/commit! db [:dx/put id-vec data])))))
