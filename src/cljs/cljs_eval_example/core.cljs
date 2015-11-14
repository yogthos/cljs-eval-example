(ns cljs-eval-example.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            [cljs.env :refer [*compiler*]]
            [cljs.pprint :refer [pprint]]))

(defn eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result] result)))

(defn editor-did-mount [input]
  (fn [this]
    (let [cm (.fromTextArea  js/CodeMirror
                             (reagent/dom-node this)
                             #js {:mode "clojure"
                                  :lineNumbers true})]
      (.on cm "change" #(reset! input (.getValue %))))))

(defn editor [input]
  (reagent/create-class
   {:render (fn [] [:textarea
                            {:default-value ""
                             :auto-complete "off"}])
    :component-did-mount (editor-did-mount input)}))

(defn render-code [this]
  (->> this reagent/dom-node (.highlightBlock js/hljs)))

(defn result-view [output]
  (reagent/create-class
   {:render (fn []
              [:pre>code.clj
               (with-out-str (pprint @output))])
    :component-did-update render-code}))

(defn home-page []
  (let [input (atom nil)
        output (atom nil)]
    (fn []
      [:div
       [editor input]
       [:div
        [:button
         {:on-click #(reset! output (eval-str @input))}
         "run"]]
       [:div
        [result-view output]]])))

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
