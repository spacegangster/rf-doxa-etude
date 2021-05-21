(ns rf-doxa.views.facade
  (:require
   [re-frame.core :as rf]
   [re-com.core :as re-com :refer [at]]
   [re-com.buttons]
   [reagent.core :as rc]
   [rf-doxa.styles :as styles]
   [rf-doxa.events :as events]
   [rf-doxa.routes :as routes]
   [rf-doxa.subs :as subs]))



;; home

(defn tab:complete []
  [re-com/hyperlink
   :src      (at)
   :label    "complete"
   :on-click #(rf/dispatch [:evt.sys/navigate {:nav/tab :nav.tab/complete}])])

(defn tab:due []
  [re-com/hyperlink
   :src      (at)
   :label    "due"
   :on-click #(rf/dispatch [:evt.sys/navigate {:nav/tab :nav.tab/due}])])

(defn tab:all []
  [re-com/hyperlink
   :src      (at)
   :label    "all"
   :on-click #(rf/dispatch [:evt.sys/navigate {:nav/tab :nav.tab/all}])])



(defn home-title []
  [re-com/title
   :src (at)
   :label "doxa powered todo list"
   :level :level1
   :class (styles/level1)])


(defn on-task-status-change [id status] ; & [?opt-on-done]
  (rf/dispatch [:evt.db/put [[:db/id id] :m/status status]]))

(defn on-task-gist-change [id gist] ; & [?opt-on-done]
  (rf/dispatch [:evt.db/put [[:db/id id] :m/gist gist]]))

(defn dispatch:delete-task [id]
  (rf/dispatch [:evt.db/delete-task {:evt/eid id}]))

(defn dispatch:add-task []
  (rf/dispatch [:evt.db/add-task]))


(defn task
  [{gist :m/gist
    status :m/status
    id :db/id
    :as task}]
  [re-com/h-box
   :class (styles/task)
   :children
   [[re-com/checkbox
     :model status
     ;:attr {:checked status}
     :on-change (rc/partial on-task-status-change id)]
    [re-com/input-text
     :on-change (rc/partial on-task-gist-change id)
     :model gist]
    [re-com.buttons/button
     :label "[x]"
     :on-click (rc/partial dispatch:delete-task id)]]])

(defn todos-raw [todos]
  [re-com/v-box
   :class (styles/todo-list)
   :children
   (for [t todos]
     ^{:key t}
     [task t])])


(defn on-search-change [new-val]
  (rf/dispatch [:evt.db/simple-assoc {:evt/prop :db/search-string, :evt/value new-val}]))

(defn on-search-alter [new-val]
  (rf/dispatch [:evt.db/simple-assoc {:evt/prop  :db/search-string, :evt/value new-val}]))



(defn search []
  (let [sub:search (rf/subscribe [:subs.db/search-string])]
    (fn []
      [re-com/h-box
       :children
       [[re-com/input-text
         :model       @sub:search
         :placeholder "search"
         :on-change   on-search-change
         :on-alter (fn [v] (on-search-alter v) v)]
        [re-com/button
         :label "[reset]"
         :on-click (rc/partial on-search-alter "")]]])))

(defn tabs []
  (let [sub:at (rf/subscribe [:subs.db/active-tab])]
    (fn []
      (let [a-tab @sub:at]
        [re-com/h-box
         :gap "16px"
         :children
         [[tab:all (= :tabs/all a-tab)]
          [tab:due (= :tabs/due a-tab)]
          [tab:complete(= :tabs/complete a-tab)]]]))))

(defn todos []
  (let [sub:todos (rf/subscribe [:subs.db.todos/filtered])]
    (fn []
      [re-com/v-box
       :gap "8px"
       :children
       [[tabs]
        [search]
        [re-com/button
         :on-click dispatch:add-task
         :label "Add task"]
        ^{:key @sub:todos}
        [todos-raw @sub:todos]]])))


(defn face []
  [re-com/v-box
   :src (at)
   :height "100%"
   :align :center
   :children [[home-title]
              [todos]]])
