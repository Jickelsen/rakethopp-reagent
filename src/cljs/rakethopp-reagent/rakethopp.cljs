(ns rakethopp-reagent.rakethopp
  (:require [clojure.string :as string]
            [reagent.core :as r :refer [atom]]))

(enable-console-print!)

(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])

(defn start []
  (r/render-component [simple-component]
                            (.-body js/document)))
