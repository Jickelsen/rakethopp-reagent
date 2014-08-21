(ns rakethopp-reagent.rakethopp
  (:require [rakethopp-reagent.session :as session]
            [clojure.string :as string]
            [reagent.core :as r :refer [atom]]
            [secretary.core :as secretary :refer [dispatch!]]
            [secretary.core :include-macros true :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(enable-console-print!)

(defn empty-section []
  [:div])

(defn games-section []
  [:div
   [:i "Some stuff about games"]])

(defn ixd-section []
  [:div
   [:i "Some stuff about IxD"]])

(defn about-section []
  [:div
   [:i "Some stuff about me!"]])

(defroute "/" []
  (session/put! :current-page empty-section))

(defroute "/games" []
  (session/put! :current-page games-section))

(defroute "/ixd" []
  (session/put! :current-page ixd-section))

(defroute "/about" []
  (session/put! :current-page about-section))

(def current-page (atom nil))

(defn page []
  [(session/get :current-page)])

(defn bs-main []
  [:div.container
   [:div.jumbotron
    [page]
    [:div.row
     [:div.col-sm-4
      [:a {:href "#games" :on-click #(dispatch! "/games")}
       [:h2 "games"]]]
     [:div.col-sm-4
      [:a {:href "#ixd" :on-click #(dispatch! "/ixd")}
       [:h2 "interaction"]]]
     [:div.col-sm-4
      [:a {:href "#about" :on-click #(dispatch! "/about")}
       [:h2 "about"]]]]]])

(defn init![]
  (secretary/set-config! :prefix "#")
  (session/put! :current-page empty-section)
  (r/render-component
   [bs-main]
   (.-body js/document)))

(init!)

;; History configuration from Secretary docs
(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))
