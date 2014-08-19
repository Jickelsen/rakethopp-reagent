(defproject rakethopp-reagent "0.1.0-SNAPSHOT"
  :description "My personal portfolio page made with ClojureScript and Dan Holmsand's Reagent. Email jickelsen@rakethopp.se/jacobmi@tii.se or send a tweet to @jickelsen."
  :url "http://rakethopp.se"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}


  ;; CLJ AND CLJS source code path
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [whoops/reagent "0.4.3"]]

  ;; lein-cljsbuild plugin to build a CLJS project
  :plugins [[lein-cljsbuild "1.0.3"]]

  ;; cljsbuild options configuration
  :cljsbuild {:builds
              [{;; CLJS source code path
                :source-paths ["src/cljs"]

                ;; Google Closure (CLS) options configuration
                :compiler {;; CLS generated JS script filename
                           :output-to "resources/public/js/rakethopp.js"
                           :output-dir "resources/public/js/out"

                           ;; minimal JS optimization directive
                           :optimizations :whitespace

                           ;; let's not forget to include React
                           :preamble ["reagent/react.js"]

                           ;; generated JS code prettyfication
                           :pretty-print true

                           ;; source maps are cool
                           :source-map "resources/public/js/rakethopp.js.map"}}]})
