(ns rf-doxa.microbench
  "Hey this a microbench from a non-specialist in benchmarks."
  (:require [ribelo.doxa :as dx]))


(def tasks-bases
  [{:dst-tag-id -2 :gist "Check out local communities"}

   {:dst-tag-id -2 :gist "Press \"expand all\" to archive tasks and see their descriptions"}

   {:dst-tag-id -2 :gist "Check out Lightpad's Twitter @lightpad_ai"
    :content    "<p>in case if you didn't press \"expand all\"</p>"}

   {:dst-tag-id -2  :gist "Groceries"}
   {:dst-tag-id -2  :gist "Feed the cat"}
   {:dst-tag-id -2  :gist "Water the plants"}
   {:dst-tag-id -2  :gist "Eat a fruit"}


   ; Personal
   {:dst-tag-id -3   :gist "Compliment a friend"}


   ; The Automaton Project
   {:dst-tag-id -4, :demo/day-offset 0, :gist "Steam automaton: pros and cons"
    :content
                (str "<p>Automatons are somewhat self-operative machines, earliest"
                     " notes about the earliest of them could be traced back to 18-th century."
                     "https://en.wikipedia.org/wiki/Automaton" "Check out this Wikipedia article."
                     "<br>Here are a few tasks fictionalizing a steam-punk automaton research project.</p>")
    :ui/expanded? true}

   {:dst-tag-id -4, :demo/day-offset 1, :gist "Automaton mk1 design draft"}
   {:dst-tag-id -4, :demo/day-offset 1, :gist "Mk1 steam core placement"}
   {:dst-tag-id -4, :demo/day-offset 1, :gist "Mk1 center of mass calculations"}

   {:dst-tag-id -4, :demo/day-offset 2, :gist "Mk1 prototype assembly"}
   {:dst-tag-id -4, :demo/day-offset 2, :gist "Mk1 course stability trials"}

   {:dst-tag-id -4, :demo/day-offset 3, :gist "Mk1 applied industrial trials"}
   {:dst-tag-id -4, :demo/day-offset 3, :gist "Mk1 coal factory operation trials"}
   {:dst-tag-id -4, :demo/day-offset 3, :gist "Mk1 sawmill operation trials"}
   {:dst-tag-id -4, :demo/day-offset 3, :gist "Mk1 hothouse operation trials"}

   {:dst-tag-id -4, :demo/day-offset 5, :gist "Mk1 trials results collection and evaluation"}
   {:dst-tag-id -4, :demo/day-offset 5, :gist "Mk1 stability issues assessment"}
   {:dst-tag-id -4, :demo/day-offset 5, :gist "Mk1 design optimization research"}

   {:dst-tag-id -4, :demo/day-offset 6, :gist "Automaton mk2 design draft, address stability"}
   {:dst-tag-id -4, :demo/day-offset 6, :gist "Advanced pneumatics for Mk2"}

   {:dst-tag-id -4, :demo/day-offset 8, :gist "Mk2 prototype assembly"}
   {:dst-tag-id -4, :demo/day-offset 8, :gist "Mk2 applied industrial trials"}
   {:dst-tag-id -4, :demo/day-offset 8, :gist "Mk2 trials results collection and evaluation"}

   {:dst-tag-id -4, :demo/day-offset 9, :gist "Mk2 industrial version design"
    :content "<p>Mk2 was a success. Industrial version only needs better protection from flying particles.</p>"}
   {:dst-tag-id -4, :demo/day-offset 9, :gist "Mk2 industrial version prototype"}

   {:dst-tag-id -4, :demo/day-offset 10, :gist "Mk2 industrial small batch production for local factories"}

   {:dst-tag-id -4, :demo/day-offset 12, :gist "Automaton Mk3-precision design"}
   {:dst-tag-id -4, :demo/day-offset 13, :gist "Mk3-precision advanced optics research"}
   {:dst-tag-id -4, :demo/day-offset 13, :gist "Mk3-precision precision tools research"}

   {:dst-tag-id -4, :demo/day-offset 14, :gist "Mk3 prototype assembly"}
   {:dst-tag-id -4, :demo/day-offset 14, :gist "Mk3 prototype trials"}
   {:dst-tag-id -4, :demo/day-offset 14, :gist "Mk3 engineering applications research"}
   {:dst-tag-id -4, :demo/day-offset 14, :gist "Mk3 house applications research"}
   {:dst-tag-id -4, :demo/day-offset 14, :gist "Mk3 medical applications"}])


(defn rnd-task []
  (assoc (rand-nth tasks-bases) :db/id (rand-int 10000000)))


(defn gen-tasks [n]
  (repeatedly n rnd-task))

(def tasks1 (vec (gen-tasks 100000)))

(def db1
  (dx/create-dx tasks1))

(defn bench []
  (time
    (let [res
          (dx/q [:find ?e
                 :where
                 [?e :gist ?g]
                 ;[?e :demo/day-offset 1]
                 [(re-find #"Mk3" ?g)]]
                db1)]
      (prn (count res)))))

(comment
  (keys tasks1)
  (keys db1)
  (bench)
  ; On MBP 2018 this takes 500+ ms usually

  (time
    (def f1 (filterv #(re-find #"Mk3" (:gist %)) tasks1))))
  ; MBP 2018 ~40ms"


