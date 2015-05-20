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

(defn work-detail [work]
  [:div.row
   [:h2 ((first work) :title)]
   [:p ((first work) :description) ]
   (if-not (empty? (first work) :url)
     [:a {:href ((first work) :url)} [:p ((first work) :url_text)]]
     [:div])
   (for [x (vec (range ((first work) :num_img)))]
     [:div.col-sm-3.col-xs-6
      [:a.thumbnail {:data-lightbox ((first work) :title_short) :href (str "/img/" ((first work) :title_short) "_" x ".jpg")}
       ;;         (.error js/console (str "hello" x))
       [:img {:src (str "/img/" ((first work) :title_short) "_" x ".jpg")}]]])])


(defn work-splash [work]
  [:div.col-sm-3.col-xs-6
   [:a.thumbnail {:href (str "#" (session/get :current-type) "/" (work :title_short))}
    [:img {:src (str "/img/" (work :title_short) "_th.jpg")}]
    [:div.caption
     [:h6 (work :title)]]]])

(defn works-sec []
  [:div.col-sm-12
   [ctg {:transitionName "example" :transitionLeave false}
    (if-not (or (empty? (session/get :works)) (empty? (session/get :current-detail)))
;;      (.error js/console [work-detail (filter (= (get (session/get :current-detail)(session/get :works))))])
     ^{:key (session/get :current-detail)}[work-detail (filterv #(= (:title_short %) (session/get :current-detail)) (session/get :works) )]
     [:div])]
   [:div.row
    [ctg {:transitionName "example"}
     (for [work (session/get :works)]
       ^{:key work} [work-splash work])]]])

(defn about-sec []
  [:div
   [:p "I'm Jacob. I do research and development for " [:a {:href "http://www.tii.se"} "the Interactive Institute" ] " in Göteborg. This usually means I'm building AR and VR " [:a {:href "https://www.tii.se/people/jacob-michelsen#projects"} "prototypes"] ". I also write " [:a {:href "http://gup.ub.gu.se/publication/196447"} "papers"]"."]])

(defroute "/" []
  (session/put! :current-page empty-sec))

(defroute "/games" []
  (session/ajax-put! :url "/php/getworks.php" :work-type "games" )
  (session/put! :current-page works-sec)
  (session/put! :current-type "games")
  (session/put! :current-detail []))

(defroute "/games/:game" {:as params}
  (session/ajax-put! :url "/php/getworks.php" :work-type "games")
  (session/put! :current-page works-sec)
  (session/put! :current-type "games")
  (session/put! :current-detail (:game params)))

(defroute "/projects" []
  (session/ajax-put! :url "/php/getworks.php" :work-type "projects")
  (session/put! :current-page works-sec)
  (session/put! :current-type "projects")
  (session/put! :current-detail []))

(defroute "/projects/:project" {:as params}
  (session/ajax-put! :url "/php/getworks.php" :work-type "projects")
  (session/put! :current-page works-sec)
  (session/put! :current-type "projects")
  (session/put! :current-detail (:project params)))

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
