(ns rf-doxa.events
  (:require
    [re-frame.core :as rf]
    [rf-doxa.db :as db]
    [rf-doxa.effects]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [ribelo.doxa :as dx]))


#_(rf/reg-event-fx
    :evt.sys/init-db
    (fn [_ [_ ?loaded-db]]
      (let [db (dx/commit db/default-db (db/default-tasks-transactions))]
        ; showing the new :fx fx handler
        {:fx [[:db db]
              [:fx.dx/register-db [:dx.db/app (fn [] re-frame.db/app-db)]]]})))


(rf/reg-event-fx
  :evt.sys/init-db
  (fn [_ [_ ?loaded-db]]
    (let [db (dx/create-dx db/default-tasks {:db/db-id :db.db-id/main, :with-diff? true})
          db (merge db db/default-db)]
      ; showing the new :fx fx handler
      {:db db
       :fx [[:fx.dx/register-db [:dx.db/app (fn [] re-frame.db/app-db)]]]})))

(comment
  (rf/dispatch [:evt.sys/init-db])
  (deref re-frame.db/app-db)
  (dx/pull @re-frame.db/app-db [:gist] [:db/id 1]))


(rf/reg-event-fx
  :evt.sys/post-diff
  (fn [_ [_ diff]]
    {:fx.server/post-diff diff}))



(rf/reg-event-fx
  :evt.sys/navigate
  (fn [_ [_ nav-map]]
    {:fx/navigate nav-map}))


(rf/reg-event-fx
  :evt.db/set-active-tab
  (fn [{:keys [db]} [_ active-panel]]
    {:db (assoc db :db/active-tab active-panel)}))


(defn db->new-id [db]
  (if-let [cur-id (:db/next-id db)]
    [(str "client-" cur-id)
     (update db :db/next-id inc)]
    (let [idx (:db/id db)
          cur-id (inc (apply max (keys idx)))]
      [(str "client-" cur-id)
       (assoc db :db/next-id (inc cur-id))])))

(rf/reg-event-db
  :evt.db/simple-assoc
  (fn [db [_ {prop :evt/prop value :evt/value}]]
    (assoc db prop value)))

(comment
  (rf/dispatch [:evt.db/simple-assoc {:evt/prop :db/search-string :evt/value "333"}]))


(rf/reg-event-fx
  :evt.db/incoming-diff
  [re-frame.core/trim-v]
  (fn [{db :db, :as cofx} [{:diff/keys [edits path op-type value time]}]]
    {:db (dx/patch db (or edits [[path op-type value]]) time)}))

(comment
  ; patch whole map
  (rf/dispatch
    [:evt.db/incoming-diff
     {:diff/edits [[[:db/id 1] :r {:m/gist "buy milk e"}]]
      :diff/time  1621601148627}])

  ; patch a simple value
  (rf/dispatch
    [:evt.db/incoming-diff
     {:diff/edits [[[:db/id 1 :m/gist] :r "buy milk e"]]
      :diff/time  1621601148627}]))


(rf/reg-event-fx
  :evt.db/add-task
  (fn [{db :db, :as cofx}]
    (let [[new-id db]  (db->new-id db)
          put-vec [:dx/put {:db/id new-id :m/gist "" :m/status false}]
          db (dx/commit db put-vec)]
      {:db db})))

(rf/reg-event-fx
  :evt.db/delete-task
  [re-frame.core/trim-v]
  (fn [{db :db, :as cofx} [{eid :evt/eid, :as evt}]]
    (prn ::delete-task evt)
    {:db (dx/commit db [:dx/delete [:db/id eid]])}))


(rf/reg-event-fx
  :evt.db/put
  [re-frame.core/trim-v]
  (defn db-put
    "put-able is map or a vector like: [[:db/id 1] :prop-name val]"
    [{db :db, :as cofx} [put-able]]
    (prn ::put put-able)
    (let [put-vec (if (map? put-able)
                    [:dx/put put-able]
                    (into [:dx/put] put-able))
          db (dx/commit db put-vec)]
      {:db db})))

















