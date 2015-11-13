(ns cljs-eval-example.prod
  (:require [cljs-eval-example.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
