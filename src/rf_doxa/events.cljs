(ns rf-doxa.events
  (:require
    [re-frame.core :as rf]
    [rf-doxa.db :as db]
    [rf-doxa.effects]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [ribelo.doxa :as dx]))


(rf/reg-event-fx
  :evt.sys/init-db
  (fn [_ [_ ?loaded-db]]
    (let [db (dx/commit db/default-db (db/default-tasks-transactions))]
      ; showing the new :fx fx handler
      {:fx [[:db db]
            [:fx.dx/register-db [:dx.db/app (fn [] re-frame.db/app-db)]]]})))

(comment
  (rf/dispatch [:evt.sys/init-db])
  (deref re-frame.db/app-db))

(rf/reg-event-fx
  :evt.sys/navigate
  (fn [_ [_ page]]
    {:fx/navigate page}))


(rf/reg-event-fx
 ::set-active-panel
 (fn [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)}))
