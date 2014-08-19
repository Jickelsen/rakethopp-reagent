(ns rakethopp-reagent.rakethopp
  (:require [clojure.string :as string]
            [reagent.core :as r :refer [atom]]))

(enable-console-print!)

(defn sub-headers []
  [:div
   [:h4 "Subheading 1"]
   [:p "Subheading text"]
   [:h4 "Subheading 2"]
   [:p "Subheading text"]
   [:h4 "Subheading 3"]
   [:p "Subheading text"]])

(defn bs-main []
  [:div.container
   [:div.jumbotron
    [:h1 "Jumbotron heading rawr"]]
   [:div.row
    [:div.col-md-6
     [sub-headers]]
    [:div.col-md-6
     [sub-headers]]]])

(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])

(defn start []
  (r/render-component [bs-main]
                            (.-body js/document)))
