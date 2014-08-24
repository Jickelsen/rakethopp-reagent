(ns rakethopp-reagent.rakethopp
  (:require [rakethopp-reagent.session :as session]
            [clojure.string :as string]
            [reagent.core :as r :refer [atom]]
            [secretary.core :as secretary :refer [dispatch!]]
            [secretary.core :include-macros true :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ajax.core :refer [ajax-request
                               json-format]])
  (:import goog.History))

(enable-console-print!)

(def ctg (-> js/React (aget "addons") (aget "CSSTransitionGroup")))

(defn empty-sec []
  [:div])

(defn games-sec []
  (ajax-request {:uri "/php/getgames.php" :method "POST"
                 :handler (fn [[ok res]]
                            (if ok
                              (session/put! :games res)
                              (.error js/console (str res))))
                 :format (json-format {:keywords? true})})
  (fn []
    [:ul
     [ctg {:transitionName "example"}
      (for [game (session/get :games)]
        ^{:key game} [:li "Item " (game :title)])]]))

(defn ixd-sec []
  [:div
   [:i "Some stuff about IxD"]])

(defn about-sec []
  [:div
   [:i "Some stuff about me!"]])

(defroute "/" []
  (session/put! :current-page empty-sec))

(defroute "/games" []
  (session/put! :current-page games-sec))

(defroute "/ixd" []
  (session/put! :current-page ixd-sec))

(defroute "/about" []
  (session/put! :current-page about-sec))

(defn page []
  [(session/get :current-page)])

(defn bs-main []
  [:div.container
   [:div.jumbotron
    [page]
    [:div.row
     [:div.col-sm-4
      [:a {:href "#games"}
       [:h2 "games"]]]
     [:div.col-sm-4
      [:a {:href "#ixd"}
       [:h2 "interaction"]]]
     [:div.col-sm-4
      [:a {:href "#about"}
       [:h2 "about"]]]]]])

(defn init![]
  (secretary/set-config! :prefix "#")
  (session/put! :current-page empty-sec)
  (r/render-component
   (fn [][bs-main])
   (.-body js/document)))

(init!)

;; History configuration from Secretary docs
(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))
