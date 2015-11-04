(ns url-shortener.core-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :refer [pprint]]
            [url-shortener.core :refer :all]))

(defn stateful-mock-fn
  "Returns a stateful vararg function, which returns on each call
  a value from sequence s in the same order provided, then always nil"
  [s]
  (let [results (atom (cons nil s))]
    (fn [& _]
      (first (swap! results next)))))

(defn pow [n p]
  (-> (partial * n)
      (iterate 1)
      (nth p)))

(deftest can-pow
  (are [r n p] (= r (pow n p))
               1 2 0
               4 2 2
               27 3 3))

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
  (with-redefs [database (ref {})
                random-string (stateful-mock-fn ["234567" "234567" "765432"])]
    (let [short1 (shorten-url "lalala")
          short2 (shorten-url "lalala")]
      (is (= short1 "234567"))
      (is (= short2 "765432"))
      (is (= @database {"234567" "lalala"
                        "765432" "lalala"})))))

(deftest can-shorten-url-concurrently
  (let [old-ref-set ref-set]
    (with-redefs [CODE_LENGTH 1
                  database (ref {})
                  ref-set (fn [r v]
                            (Thread/sleep 1)
                            (old-ref-set r v))]
      (let [urls (map str (range (pow (count ALPHABET) CODE_LENGTH)))
            shorts (pmap shorten-url urls)]
        (is (= (count urls) (count (set shorts))))))))

#_(deftest can-shorten-url-debug-concurrently
    (let [old-ref-set ref-set]
      (with-redefs [CODE_LENGTH 1
                    database (ref {})
                    ref-set (fn [r v]
                              (Thread/sleep 1)
                              (old-ref-set r v))]
        (let [urls (map str (range (pow (count ALPHABET) CODE_LENGTH)))
              shorts (pmap shorten-url-debug urls)]
          (is (= (count urls) (count (set (map :result shorts)))))
          (pprint {:top-attempts   (take 10 (sort-by :attempts > shorts))
                   :top-collisions (take 10 (sort-by :collisions > shorts))})))))

(deftest can-get-url
  (with-redefs [database (ref {"234567" "lalala"})]
    (is (= "lalala" (get-url "234567")))
    (is (= nil (get-url "765432"))))
  (with-redefs [database (ref {})]
    (let [short (shorten-url "lalala")
          original-url (get-url short)]
      (is (= "lalala" original-url)))))