(ns rf-doxa.logging)

(defn tojs [v]
  (if (keyword? v)
    (pr-str v)
    (clj->js v)))

(defn debug [& values]
  (apply js/console.debug (mapv tojs values)))

(defn log [& values]
  (apply js/console.log (mapv tojs values)))

(def -log (atom []))
(defn mem [& values]
  (swap! -log conj values))

(defn mem-print [reset?]
  (doseq [vals @-log]
    (apply log vals))
  (when reset?
    (reset! -log []))
  nil)

(def ^:private -trace
  (if (js* "'trace' in window.console")
    js/console.trace
    (do (println "console.trace not supported, falling back to debug")
        js/console.debug)))

(defn trace [& values]
  (apply -trace (mapv tojs values)))

(defn info [& values]
  (apply js/console.info (mapv tojs values)))

(defn warn [& values]
  (apply js/console.warn (mapv tojs values)))

(defn error [& values]
  (apply js/console.error (mapv tojs values)))

