(ns user
  (:require [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.test :as test]
            [url-shortener.core :as core]
            [schema.core :as s]))

(defn run-app []
  (core/-main))

(defn run-tests []
  (s/with-fn-validation
    (test/run-all-tests #"url-shortener\..*")))

(defn app []
  (refresh :after 'user/run-app))

(defn tests []
  (refresh :after 'user/run-tests))
