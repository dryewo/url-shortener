(ns url-shortener.core
  (:gen-class)
  (:require [schema.core :as s]))

(defn random-string
  [n]
  (let [chars "23456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
        result (take n (repeatedly #(rand-nth chars)))]
    (apply str result)))

(def database (atom {}))

(s/defn shorten-url :- s/Str
  [url :- s/Str]
  (let [short (random-string 6)]
    (swap! database assoc short url)
    short))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
