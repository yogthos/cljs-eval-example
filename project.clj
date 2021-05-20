(defproject cljs-eval-example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [ring-server "0.5.0"]
                 [reagent "1.0.0"]                 
                 [ring "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [hiccup "1.0.5"]
                 [yogthos/config "1.1.7"]
                 [org.clojure/clojurescript "1.10.844"
                  :scope "provided"]
                 [metosin/reitit "0.5.12"]]

  :jvm-opts ["-Xmx1G"]
  
  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.4.6"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler cljs-eval-example.handler/app
         :uberwar-name "cljs-eval-example.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "cljs-eval-example.jar"
  :main cljs-eval-example.server
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  [[:css {:source "resources/public/css/site.css"
          :target "resources/public/css/site.min.css"}]]

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to        "target/cljsbuild/public/js/app.js"
              :output-dir       "target/cljsbuild/public/js"
              :source-map       "target/cljsbuild/public/js/app.js.map"
              :optimizations :advanced
              :infer-externs true
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "cljs-eval-example.core/mount-root"}
             :compiler
             {:main "cljs-eval-example.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}}}

  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl
                      ]
   :css-dirs ["resources/public/css"]
   :ring-handler cljs-eval-example.handler/app}

  :profiles {:dev {:repl-options {:init-ns cljs-eval-example.repl}
                   :dependencies [[cider/piggieback "0.5.2"]
                                  [binaryage/devtools "1.0.2"]
                                  [prone "2020-01-17"]
                                  [figwheel-sidecar "0.5.20"]
                                  [nrepl "0.8.3"]
                                  [pjstadig/humane-test-output "0.10.0"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.20"]
]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
