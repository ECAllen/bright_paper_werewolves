(ns bright-paper-werewolves.prod
  (:require [bright-paper-werewolves.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
