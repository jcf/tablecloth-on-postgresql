(ns top.postgres
  (:refer-clojure :exclude [count])
  (:require
   [honey.sql :as sql]
   [next.jdbc :as jdbc]
   [next.jdbc.date-time]
   [next.jdbc.result-set :as jdbc.result-set]
   [next.jdbc.types :as jdbc.types]))

(def default-execute-opts
  "Default options to pass to all executions via `next.jdbc`.

  Ensures column names are converted to unqualified keywords to match any
  aliases used in `SELECT` statements (which is consistent with the behaviour of
  `tech.v3.dataset.sql`."
  {:builder-fn jdbc.result-set/as-unqualified-lower-maps})

(def read-only-connect-opts
  "Options map to pass to `connect!` when loading datasets from a SQL database.

  Auto-commit is disabled as to allow batched inserts. We run a read-only
  connection however to be safe by default. You might be bolder than me."
  {:auto-commit false
   :read-only   true})

;;; ----------------------------------------------------------------------------
;;; Enum

;; TODO Appease clj-kondo: `jdbc.types/as-other` is unresolved.
(defn keyword->enum
  [k]
  (-> k name jdbc.types/as-other))

;;; ----------------------------------------------------------------------------
;;; Connect!

(defn get-datasource
  []
  (jdbc/get-datasource {:dbtype   "postgres"
                        :dbname   "tablecloth"
                        :host     "127.0.0.1"
                        :user     "scientist"
                        :password "please"}))

(defn get-connection
  (^java.sql.Connection [source]
   (get-connection source {}))
  (^java.sql.Connection [source opts]
   (jdbc/get-connection source opts)))

(defn connect!
  (^java.sql.Connection []
   (connect! {}))
  (^java.sql.Connection [opts]
   (get-connection (get-datasource) opts)))

;;; ----------------------------------------------------------------------------
;;; Execute!

(defn execute!
  ([conn query]
   (execute! conn query default-execute-opts))
  ([conn query opts]
   (jdbc/execute! conn (sql/format query) opts)))

(defn execute-batch!
  ([conn query]
   (execute-batch! conn query default-execute-opts))
  ([conn query opts]
   (jdbc/execute-batch! conn (sql/format query) opts)))

(defn execute-one!
  ([conn query]
   (execute-one! conn query default-execute-opts))
  ([conn query opts]
   (jdbc/execute-one! conn (sql/format query) opts)))

;;; ----------------------------------------------------------------------------
;;; Count

(defn count
  [conn table-name]
  (let [query {:select [[[:count 1] :n]]
               :from   [table-name]}]
    (-> conn (execute-one! query) :n)))
