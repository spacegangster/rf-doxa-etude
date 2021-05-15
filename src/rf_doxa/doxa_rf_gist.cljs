(ns rf-doxa.doxa-rf-gist
  "Adapted from
   https://gist.github.com/ribelo/9d1fdfa05732d3e0a5c4549e23c8d5f2"
  (:require [ribelo.doxa :as dx]
            [re-frame.core :as rf]))

;; despite general good practice, in my case it doesn't work to keep all data in
;; one db. in every application I have to save some data and removing from store
;; data concerning ui and other stuff is not cool.

;; declare mutiple db

;(def default-db       (dx/create-dx))
;; register db
(dx/reg-dx! :dx.db/app re-frame.db/app-db)

;; with-dx is a macro that assigns a db named keword to ?symbol
;; this allows you to conveniently use multiple db's simultaneously without
;; requiring them

(comment
  (dx/with-dx [?symbol keyword?] ...))


(rf/reg-fx
 :fffx.dx/put
 (fn [put-tx]
   (dx/commit! :dx.db/app [:dx/put id-vec data])))

(dx/with-dx [db :dx.db/app]
  (dx/commit! db [[:dx/put [:db/id :e.id.task/one] {:gist "Buy milk"}]]))

(comment
  (deref (get @dx/dxs_ :dx.db/app))
  (reset! dx/dxs_ {})
  (deref re-frame.db/app-db)
  (dx/pull @re-frame.db/app-db [:gist] [:db/id 1]))

;; from now on you can use `commit` in re-frame effects

(rf/reg-event-fx
 :evt.db/create-task
 (fn [cofx [_ task]]
;; {:fx [[:commit [:store-name [transaction or transactions]]]]}
   {:fx.dx/put [[:db/id :foo] task]}))

;; if you want to retrieve data from default-db, you can use a simple
;; reg-sub

(rf/reg-sub
 :sub.dx/tasks
 (fn [db [_ foo]]
   (dx/pull db [:bar] [:db/id foo])))


;; or you can use `reg-sub-raw` with `dx/with-dx`
(rf/reg-sub-raw
 :sub.raw.dx/tasks
 (fn [_ [_ foo]]
   (reagent.ratom/reaction
    (dx/with-dx [dx_ :app]
      (dx/pull @dx_ [:bar] [:db/id foo])))))
