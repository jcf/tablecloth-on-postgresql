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

(defn get-datasource
  []
  (jdbc/get-datasource {:dbtype   "postgres"
                        :dbname   "tablecloth"
                        :host     "127.0.0.1"
                        :user     "scientist"
                        :password "please"}))

(defn get-connection
  ^java.sql.Connection
  [source]
  (jdbc/get-connection source))

(defn connect!
  ^java.sql.Connection
  []
  (get-connection (get-datasource)))

(defn get-read-only-connection
  ^java.sql.Connection
  [source]
  (jdbc/get-connection source {:auto-commit false
                               :read-only   true}))

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

(defn keyword->enum
  [k]
  (-> k name jdbc.types/as-other))

(defn count
  [conn table-name]
  (let [query {:select [[[:count 1] :n]]
               :from   [table-name]}]
    (-> conn (execute-one! query) :n)))
