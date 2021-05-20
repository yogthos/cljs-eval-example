(ns cljs-eval-example.handler
  (:require
   [reitit.ring :as reitit-ring]
   [cljs-eval-example.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to cljs-eval-example"]
   [:p "please wait while Figwheel is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css
    (if (env :dev) "/css/site.css" "/css/site.min.css")
    "//cdnjs.cloudflare.com/ajax/libs/codemirror/5.8.0/codemirror.min.css"
    "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.9.1/styles/default.min.css"
    (if (env :dev) "css/site.css" "css/site.min.css"))
   (include-js
    "//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.9.1/highlight.min.js"
    "//cdnjs.cloudflare.com/ajax/libs/codemirror/5.8.0/codemirror.min.js"
    "//cdnjs.cloudflare.com/ajax/libs/codemirror/5.8.0/mode/clojure/clojure.min.js")])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))


(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/items"
      ["" {:get {:handler index-handler}}]
      ["/:item-id" {:get {:handler index-handler
                          :parameters {:path {:item-id int?}}}}]]
     ["/about" {:get {:handler index-handler}}]])
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
