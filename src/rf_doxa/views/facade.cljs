(ns rf-doxa.views.facade
  (:require
   [re-frame.core :as rf]
   [re-com.core :as re-com :refer [at]]
   [reagent.core :as rc]
   [rf-doxa.styles :as styles]
   [rf-doxa.events :as events]
   [rf-doxa.routes :as routes]
   [rf-doxa.subs :as subs]))



;; home

(defn link-to-about-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to About Page"
   :on-click #(rf/dispatch [:evt.sys/navigate :about])])

(defn link-to-home-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to Home Page"
   :on-click #(rf/dispatch [:evt.sys/navigate :home])])



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


(defn task
  [{gist :m/gist
    status :m/status
    id :db/id
    :as task}]
  ^{:key status}
  [re-com/h-box
   :class (styles/task)
   :children
   [[re-com/checkbox
     :model status
     :on-change (rc/partial on-task-status-change id)]
    [re-com/input-text
     :on-change (rc/partial on-task-gist-change id)
     :model gist]]])

(defn todos-raw [todos]
  [re-com/v-box
   :class (styles/todo-list)
   :children
   (for [t todos]
     ^{:key t}
     [task t])])

(defn todos []
  (let [sub:todos (rf/subscribe [:subs.db.todos/all])]
    (fn []
      [todos-raw @sub:todos])))

(defn home-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[home-title]
              [link-to-about-page]
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
