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

(defn link-to-about-page []
  [re-com/hyperlink
   :src      (at)
   :label    "complete"
   :on-click #(rf/dispatch [:evt.sys/navigate {:nav/tab :nav.tab/done}])])

(defn link-to-home-page []
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
  (let [atom:status (rc/atom status)]
    (fn []
      ^{:key @atom:status}
      [re-com/h-box
       :class (styles/task)
       :children
       [[re-com/checkbox
         :model atom:status
         ;:attr {:checked status}
         :on-change (fn [v]
                      (let [nv (swap! atom:status not)]
                        (on-task-status-change id nv)))]
        [re-com/input-text
         :on-change (rc/partial on-task-gist-change id)
         :model gist]
        [re-com.buttons/button
         :label "[x]"
         :on-click (rc/partial dispatch:delete-task id)]]])))

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
  (let [atom:search (rc/atom "")
        sub:search (rf/subscribe [:subs.db/search-string])]
    (fn []
      [re-com/h-box
       :children
       [
        [re-com/input-text
         :model       @sub:search
         :placeholder "search"
         :on-change   on-search-change
         #_#_:on-alter (fn [v]
                         (prn ::alter v)
                         (reset! atom:search v)
                         (on-search-alter v))]
        [re-com/button
         :label "[reset]"
         :on-click (rc/partial on-search-alter "")]]])))


(defn todos []
  (let [sub:todos (rf/subscribe [:subs.db.todos/filtered])]
    (fn []
      [re-com/v-box
       :children
       [[search]

        [re-com/button
         :on-click dispatch:add-task
         :label "Add task"]
        ^{:key @sub:todos}
        [todos-raw @sub:todos]]])))

(defn home-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[home-title]
              [todos]]])


(defmethod routes/panels :home-panel [] [home-panel])

;; about

(defn about-title []
  [re-com/title
   :src   (at)
   :label "This is the About Page."
   :level :level1])

(defn about-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[about-title]
              [link-to-home-page]]])

(defmethod routes/panels :about-panel [] [about-panel])

;; main

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [re-com/v-box
     :src      (at)
     :height   "100%"
     :children [(routes/panels @active-panel)]]))
