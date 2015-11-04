(ns url-shortener.core
  (:gen-class)
  (:require [schema.core :as s]))

(def ALPHABET "23456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ")
(def CODE_LENGTH 6)

(defn random-string
  [n]
  (->> (repeatedly #(rand-nth ALPHABET))
       (take n)
       (apply str)))

(def database (ref {}))

(s/defn shorten-url :- s/Str
  [url :- s/Str]
  (let [result (ref nil)]
    (dosync
      (let [short (some #(when-not (contains? @database %) %)
                        (repeatedly #(random-string CODE_LENGTH)))]
        (ref-set result short)
        (alter database assoc short url)))
    @result))

#_(s/defn shorten-url-debug :- {:result     s/Str
                                :collisions s/Int
                                :attempts   s/Int}
    [url :- s/Str]
    (let [result (ref nil)
          stats (atom {:collisions 0
                       :attempts   0})]
      (dosync
        (let [[short collision-count] (loop [i 0]
                                        (let [s (random-string CODE_LENGTH)]
                                          (if (contains? @database s)
                                            (recur (inc i))
                                            [s i])))]
          (ref-set result short)
          (alter database assoc short url)
          (swap! stats update-in [:collisions] + collision-count)
          (swap! stats update-in [:attempts] inc)))
      (merge {:result @result}
             @stats)))

(s/defn get-url :- (s/maybe s/Str)
  [short :- s/Str]
  (get @database short))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
