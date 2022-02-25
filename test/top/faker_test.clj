(ns top.faker-test
  (:require
   [clojure.test :refer [are deftest]]
   [top.faker :as sut]))

(deftest slugify
  (are [in out] (= out (sut/slugify in))
    ""                                ""
    " "                               ""
    "Foo Bar"                         "foo-bar"
    "?! You ain't seen nothing yet! " "you-ain-t-seen-nothing-yet"))
