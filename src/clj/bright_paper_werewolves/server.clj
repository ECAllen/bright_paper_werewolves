(ns bright-paper-werewolves.server
  (:require [bright-paper-werewolves.handler :refer [app]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "4000"))]
    (run-jetty app {:port port :join? false})))
