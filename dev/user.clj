(ns user
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.namespace.repl]))

(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")
(s/check-asserts true)
