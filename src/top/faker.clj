(ns top.faker
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [top.edn :as edn]))

(defn- read-fixture
  [fixture-name path]
  (get-in (edn/read (io/resource (format "fixtures/%s.edn" (name fixture-name))))
          path))

(def fake (memoize read-fixture))

(defn slugify
  [s]
  (-> s
      str/lower-case
      str/trim
      (str/replace #"[^\w+]" "-")
      (str/replace #"\-{1,}" "-")
      (str/replace #"(:?^\-)|(:?\-$)" "")))

(comment
  (slugify "?! You ain't seen nothing yet! "))
