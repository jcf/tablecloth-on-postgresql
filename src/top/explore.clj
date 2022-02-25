(ns top.explore
  "Tablecloth on PostgreSQL. Let's do some exploration!"
  (:require
   [camel-snake-kebab.core :as csk]
   [honey.sql :as sql]
   [java-time :as time]
   [tablecloth.api :as tc]
   [tech.v3.dataset.sql :as ds.sql]
   [top.postgres :as postgres]))

;;; ----------------------------------------------------------------------------
;;; SQLish

(def activities-query
  {:select     [:*]
   :from       [:activities]
   :inner-join [[:people :src]  [:= :src.id :activities.src_id]
                [:people :dest] [:= :dest.id :activities.dest_id]]
   :order-by [[:activities.created_at :asc]]})

(defn query->dataset
  [conn query]
  (ds.sql/sql->dataset conn (-> query (sql/format {:inline true}) first)
                       {:key-fn csk/->kebab-case-keyword}))

;;; ----------------------------------------------------------------------------
;;; Time

(defn beginning-of-week
  ([t]
   (beginning-of-week t :monday))
  ([t day]
   (.with t (time/day-of-week day))))

(defn weekly
  ([ds]
   (weekly ds :created-at))
  ([ds inst-column]
   (let [utc-date #(time/local-date % (time/zone-id "UTC"))
         utc-week (comp beginning-of-week utc-date)]
     (-> ds
         (tc/add-columns {:date #(map utc-date (inst-column %))
                          :week #(map utc-week (inst-column %))})
         (tc/order-by inst-column :asc)))))

(comment
  (with-open [conn (postgres/connect! postgres/read-only-connect-opts)]
    (query->dataset conn activities-query)))
