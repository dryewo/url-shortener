(ns url-shortener.core
  (:gen-class)
  (:require [schema.core :as s]))

(def ALPHABET "23456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ")

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
                        (repeatedly #(random-string 6)))]
        (ref-set result short)
        (alter database assoc short url)))
    @result))

(s/defn get-url :- (s/maybe s/Str)
  [short :- s/Str]
  (get @database short))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
