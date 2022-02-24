(ns top.domain
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as sgen]
   [clojure.string :as str]
   [top.faker :as faker]))

;;; ----------------------------------------------------------------------------
;;; Person

(s/def :person/id uuid?)

(s/def :person/username
  (s/with-gen string?
    (fn []
      (sgen/fmap #(str/join "-" (map faker/slugify %))
                 (sgen/tuple (sgen/elements (faker/fake :color [:color :name]))
                             (sgen/elements (faker/fake :weather [:weather :description]))
                             (sgen/elements (faker/fake :dog [:creature :dog :name]))
                             (sgen/elements (faker/fake :hipster [:hipster :words])))))))

(s/def :top/person
  (s/keys :req [:person/id :person/username]))

;;; ----------------------------------------------------------------------------
;;; Activity

(s/def :activity/dest uuid?)
(s/def :activity/id uuid?)
(s/def :activity/src uuid?)

(def activity-types
  #{:activity.type/like :activity.type/subscribe})

(s/def :activity/type activity-types)

(s/def :top/activity
  (s/keys :req [:activity/dest
                :activity/id
                :activity/src
                :activity/type]))


(comment
  ;; This yields 9.5 million distinct usernames.
  (count (for [a (faker/fake :color [:color :name])
               b (faker/fake :weather [:weather :description])
               c (faker/fake :dog [:creature :dog :name])
               d (faker/fake :hipster [:hipster :words])]
           [a b c d]))

  (sgen/sample (s/gen :person/username))
  (sgen/generate (s/gen :top/person))
  (sgen/generate (s/gen :top/activity)))
