(defproject rakethopp-reagent "0.2.0-SNAPSHOT"
  :description "My personal portfolio page made with ClojureScript and Dan Holmsand's Reagent. Email jickelsen@rakethopp.se/jacobmi@tii.se or send a tweet to @jickelsen."
  :url "http://rakethopp.se"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;; CLJ source code path
  :source-paths ["src/clj"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-devel "1.2.2"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-basic-authentication "1.0.5"]
                 [environ "0.5.0"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [reagent "0.5.0"]
                 [re-frame "0.2.0"]
                 [secretary "1.2.1"]
                 [cljs-http "0.1.34"]
                 ;; [cljs-ajax "0.3.10"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [postgresql "9.1-901.jdbc4"]]

  :min-lein-version "2.5.0"

  :plugins [[lein-cljsbuild "1.0.5"]
            [environ/environ.lein "0.2.1"]
            [lein-ring "0.9.4"]]

  :uberjar-name "rakethopp-standalone.jar"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/rakethopp.js"
                                        :main rakethopp-reagent.main
                                        :output-dir    "resources/public/js/out"
                                        :asset-path "js/out"
                                        :source-map    "resources/public/js/rakethopp.js.map"
                                        ;; :preamble      ["react/react.min.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:source-paths ["env/dev/clj"]
                   ;; These deps are needed if we run without the lein figwheel plugin
                   :dependencies [[figwheel "0.3.3"]
                                  [figwheel-sidecar "0.3.3"]]
                   :plugins [[lein-figwheel "0.3.3"]
                             [cider/cider-nrepl "0.9.0-SNAPSHOT"]]
                   :ring {:handler rakethopp-reagent.server/run-local}
                   ;; :env {:is-dev true}
                   :figwheel {
                              :http-server-root "public" ;; default and assumes "resources"
                              :server-port 3500 ;; default
                              :css-dirs ["resources/public/css"] ;; watch and update CSS
                              :ring-handler rakethopp-reagent.server/run-local

                              ;; Start an nREPL server into the running figwheel process
                              :nrepl-port 7900}
                   :cljsbuild {:builds {:app {;; CLJS source code path
                                              :source-paths ["env/dev/cljs"]
                                              :figwheel {}
                                              }}}}

             :uberjar {:source-paths ["env/prod/clj"]
                       ;; :env {:production true}
                       :hooks [environ.leiningen.hooks]
                       :omit-source true
                       :aot :all
                       :cljsbuild {:builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler {:optimizations :advanced
                                                        :pretty-print false}}}}}}
)
