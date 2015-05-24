(defproject rakethopp-reagent "0.2.0-SNAPSHOT"
  :description "My personal portfolio page made with ClojureScript and Dan Holmsand's Reagent. Email jickelsen@rakethopp.se/jacobmi@tii.se or send a tweet to @jickelsen."
  :url "http://rakethopp.se"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}


  ;; CLJ AND CLJS source code path
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [reagent "0.5.0"]
                 [re-frame "0.2.0"]
                 [secretary "1.2.1"]
                 [cljs-ajax "0.3.10"]]

  ;; lein-cljsbuild plugin to build a CLJS project
  :plugins [[lein-cljsbuild "1.0.5"]
            [cider/cider-nrepl "0.9.0-SNAPSHOT"]
            [lein-figwheel "0.3.3"]]

  ;; cljsbuild options configuration
  :cljsbuild {:builds
              [{;; CLJS source code path
                :source-paths ["src/cljs"]

                ;; Google Closure (CLS) options configuration
                :compiler {:main rakethopp-reagent.core
                           ;; CLS generated JS script filename
                           :output-to "resources/public/js/rakethopp.js"
                           :output-dir "resources/public/js/out"
                           :asset-path "js/out"
                           ;; minimal JS optimization directive
                           :optimizations :none
                           
                           :figwheel {}

                           ;; let's not forget to include React
;;                            :preamble ["reagent/react.js"]

                           ;; generated JS code prettyfication
                           :pretty-print true

                           ;; source maps are cool
                           :source-map "resources/public/js/rakethopp.js.map"}}]}
  :figwheel {
             :http-server-root "public" ;; default and assumes "resources" 
             :server-port 3500 ;; default
             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             :nrepl-port 7900

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log" 
             })
