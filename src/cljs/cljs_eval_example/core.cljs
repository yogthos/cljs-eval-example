(ns cljs-eval-example.core
  (:require
   [reagent.dom :as dom]
   [reagent.core :as reagent :refer [atom]]
   [cljs.js :refer [empty-state eval-str js-eval]]
   [cljs.pprint :refer [pprint]]))

(defn evaluate [s cb]
  (eval-str
     (empty-state)
     s
     nil
     {:eval       js-eval    
      :source-map true
      :context    :expr}
     cb))

(defn editor-did-mount [input]
  (fn [this]
    (let [cm (.fromTextArea  js/CodeMirror
                             (dom/dom-node this)
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
  (->> this dom/dom-node (.highlightBlock js/hljs)))

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
         {:on-click #(evaluate @input (fn [result] (reset! output (str result))))}
         "run"]]
       [:div
        [result-view output]]])))

(defn mount-root []
  (dom/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
