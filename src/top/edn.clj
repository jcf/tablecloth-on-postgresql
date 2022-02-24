(ns top.edn
  (:refer-clojure :exclude [read])
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io])
  (:import
   (java.io PushbackReader)))

(defn read
  [readable]
  (with-open [rdr (io/reader readable)]
    (edn/read {:readers *data-readers*} (PushbackReader. rdr))))
