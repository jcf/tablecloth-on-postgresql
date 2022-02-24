(ns top.seed
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as sgen]
   [top.domain]
   [top.postgres :as postgres]))

(defn seed-people!
  [conn]
  (dotimes [_ 100]
    (postgres/execute!
     conn
     {:insert-into [:people]
      :values      (-> :username
                       (sgen/hash-map (s/gen :person/username))
                       (sgen/vector-distinct {:num-elements 1000})
                       sgen/generate)
      :on-conflict []
      :do-nothing  []}))
  :done)

(defn seed-activities!
  [conn]
  (dotimes [_ 500]
    (let [people     (postgres/execute! conn {:select [:*]
                                              :from   [:people]})
          activities (into []
                           (map postgres/keyword->enum)
                           top.domain/activity-types)]
      (postgres/execute!
       conn
       {:insert-into [:activities]
        :values      (into []
                           (map (fn [_]
                                  (let [src  (rand-nth people)
                                        dest (rand-nth people)]
                                    {:dest_id (:id dest)
                                     :src_id  (:id src)
                                     :type    (rand-nth activities)})))
                           (range 5000))
        :on-conflict []
        :do-nothing  []})))
  :done)

(comment
  (with-open [conn (postgres/connect!)]
    (postgres/execute! conn {:delete-from [:people]}))

  (with-open [conn (postgres/connect!)]
    (postgres/execute! conn {:delete-from [:activities]}))

  (with-open [conn (postgres/connect!)]
    (seed-people! conn))

  (with-open [conn (postgres/connect!)]
    (seed-activities! conn))

  (with-open [conn (postgres/connect!)]
    (postgres/count conn :people))

  (with-open [conn (postgres/connect!)]
    (postgres/count conn :activities)))
