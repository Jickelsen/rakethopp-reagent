(ns rakethopp-reagent.rakethopp
  (:require [rakethopp-reagent.session :as session]
            [clojure.string :as string]
            [reagent.core :as r :refer [atom]]
            [secretary.core :as secretary :refer [dispatch!]]
            [secretary.core :include-macros true :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])

  (:import goog.History))

(enable-console-print! )

(def ctg (-> js/React (aget "addons") (aget "CSSTransitionGroup")))


(defn empty-sec []
  [:div])

(defn game-detail []
  (fn []
    [:div.row
     [:h2 ((first(session/get :game-detail)) :title)]
     [:p ((first(session/get :game-detail)) :description) ]]))

(defn game-splash [game]
  [:div.col-sm-3.col-xs-6
   [:a.thumbnail {:href (str "#games/" (game :title_short))}
    [:img {:src (str "/img/" (game :title_short) "_th.jpg")}]
    [:div.caption
     [:h6 (game :title)]]]])

(defn games-sec []
  (fn []
    [:div.col-sm-12
     (if-not (empty? (session/get :game-detail))
       [game-detail]
       [:div])
     [ctg {:transitionName "example"}
       (js/console.log (str "The game splash title is " (session/get :games)))
      (for [game (session/get :games)]
        ^{:key game} [game-splash game])]]))

(defn project-detail []
  (fn []
    [:div.row
     [:h2 ((first(session/get :project-detail)) :title)]
     [:p ((first(session/get :project-detail)) :description) ]]))

(defn project-splash [project]
  [:div.col-sm-3.col-xs-6
   [:a.thumbnail {:href (str "#projects/" (project :title_short))}
    [:img {:src (str "/img/" (project :title_short) "_th.jpg")}]
    [:div.caption
     [:h6 (project :title)]]]])

(defn project-sec []
  (fn []
    [:div.col-sm-12
     (if-not (empty? (session/get :project-detail))
       [project-detail]
       [:div])
     (for [project (session/get :projects)]
       ^{:key project} [project-splash project])]))

(defn about-sec []
  [:div
   [:p "I'm Jacob. I do research and development for " [:a {:href "http://www.tii.se"} "the Interactive Institute" ] " in Göteborg. This usually means I'm building AR and VR " [:a {:href "https://www.tii.se/people/jacob-michelsen#projects"} "prototypes"] ". I also write " [:a {:href "http://gup.ub.gu.se/publication/196447"} "papers"]"."]])

(defroute "/" []
  (session/put! :current-page empty-sec))

(defroute "/games" []
  (session/ajax-put! :url "/php/getgames.php" :kw "games" )
  (session/put! :current-page games-sec)
  (session/put! :game-detail []))

(defroute "/games/:game" {:as params}
  (session/ajax-put! :url "/php/getgames.php" :kw "games" )
  (session/ajax-put! :url "/php/getgame.php" :kw "game-detail" :param (:game params))
  (session/put! :current-page games-sec))


(defroute "/projects" []
  (session/ajax-put! :url "/php/getprojects.php" :kw "projects" )
  (session/put! :current-page project-sec))

(defroute "/projects/:project" {:as params}
  (session/ajax-put! :url "/php/getprojects.php" :kw "projects" )
  (session/ajax-put! :url "/php/getproject.php" :kw "project-detail" :param (:project params))
  (session/put! :current-page project-sec))

(defroute "/about" []
  (session/put! :current-page about-sec))

(defn page []
  [(session/get :current-page)])

(defn bs-main []
  [:div.container
   [:div.jumbotron
    [:div.row
     [:div.col-sm-12
     [:a {:href "#about"}
       [:h1 "R A K E T H O P P"]]]
     [:div.row]]
    [:div.row
     [page]]
    [:div.row
     [:div.col-sm-4
      [:a {:href "#games"}
       [:h2 "G A M E S"]]]
     [:div.col-sm-4
      [:a {:href "#projects"}
       [:h2 "O T H E R"]]]
     [:div.col-sm-4
      [:a {:href "#about"}
       [:h2 "A B O U T"]]]]]])

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
