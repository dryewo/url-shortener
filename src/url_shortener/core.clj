(ns url-shortener.core
  (:gen-class)
  (:require [schema.core :as s]))

(defn random-string
  [n]
  (let [chars "23456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
        result (take n (repeatedly #(rand-nth chars)))]
    (apply str result)))

(def database (ref {}))

(s/defn shorten-url :- s/Str
  [url :- s/Str]
  (let [result (ref nil)]
    (dosync
      (let [short (some #(when-not (contains? @database %) %)
                        (repeatedly #(random-string 6)))]
        (ref-set result short)
        (alter database assoc short url)))
    @result))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
