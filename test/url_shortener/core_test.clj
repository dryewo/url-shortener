(ns url-shortener.core-test
  (:require [clojure.test :refer :all]
            [url-shortener.core :refer :all]))

(defn stateful-mock-fn
  "Returns a stateful vararg function, which returns on each call
  a value from sequence s in the same order provided, then always nil"
  [s]
  (let [results (atom (cons nil s))]
    (fn [& _]
      (first (swap! results next)))))

(deftest can-stateful-mock-fn
  (let [f (stateful-mock-fn [1 2 3])]
    (are [r] (= r (f))
             1 2 3)))

(deftest can-random-string
  (with-redefs [rand-nth (stateful-mock-fn "23456789")]
    (is (= "23456789" (random-string 8))))
  (with-redefs [rand-int (stateful-mock-fn [0 1 2 3 4 5 6 7])]
    (is (= "23456789" (random-string 8)))))

(deftest can-shorten-url
  (with-redefs [database (atom {})
                random-string (constantly "234567")]
    (let [short (shorten-url "lalala")]
      (is (= short "234567"))
      (is (= @database {"234567" "lalala"})))))