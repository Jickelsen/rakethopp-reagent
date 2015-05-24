(ns rakethopp-reagent.core
  (:require [rakethopp-reagent.session :as session]
            [clojure.string :as string]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [secretary.core :as secretary :refer [dispatch!]]
            [secretary.core :include-macros true :refer-macros [defroute]]
            [goog.events :as events]
            [figwheel.client :as fw]
            [goog.history.EventType :as EventType])

  (:require-macros [reagent.ratom  :refer [reaction]])
  (:import goog.History))

(enable-console-print!)

;; (def ctg (-> js/React (aget "addons") (aget "CSSTransitionGroup")))
;; (def ctg (reagent/adapt-react-class (aget js/React "addons" "CSSTransitionGroup")))

(re-frame/register-sub
 :whole-db
 (fn [db]
   (reaction @db)))

(re-frame/register-handler
 :initialise-db
 (fn
   [db v]
   {:games []
    :game-details {}
    :projects []
    :project-details {}
    :current-work-type ""}))

(re-frame/register-sub
 :current-work-type
 (fn [db]
   (reaction (:current-work-type @db))))

(re-frame/register-handler
 :load-works
 (fn
   [app-state _]
   (ajax/POST "/php/getworks.php" {:params {:workType "games"}
                      :format :raw
                      :response-format :json
                      :handler #(re-frame/dispatch [:process-works-response %1 :games])
                      :error-handler #(re-frame/dispatch [:process-works-bad-response %1 :games])
                      :keywords? true})
   (ajax/POST "/php/getworks.php" {:params {:workType "projects"}
                         :format :raw
                         :response-format :json
                         :handler #(re-frame/dispatch [:process-works-response %1 :projects])
                         :keywords? true})
   app-state))

(re-frame/register-handler
  :process-works-response
  (fn
    [app-state [_ response k]]
    ;; (println "hey" response)
    (assoc-in app-state [k] response)))

(re-frame/register-handler
 :process-works-bad-response
 (fn
   [app-state [_ response k]]
   (println "Error getting" k response)
   app-state))

(re-frame/register-sub
 :works
 (fn [db]
   (reaction (:works @db))))

(re-frame/register-sub
 :current-detail
 (fn [db]
   (reaction (:works @db))))
;; -------------------------
;; Views

(defn empty-sec []
  [:div])

(defn work-detail [work]
  [:div.row
   ;; (println (re-frame/subscribe [:whole-db]))
   [:h2 ((first work) :title)]
   [:p ((first work) :description) ]
   (if-not (empty? (:url (first work)))
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
(let [works (re-frame/subscribe [:works])
        current-detail (re-frame/subscribe [:current-detail])])
  [:div.col-sm-12
   ;; [ctg {:transitionName "example" :transitionLeave false}]
   (if-not (or (empty? (session/get :works)) (empty? (session/get :current-detail)))
     ;;      (.error js/console [work-detail (filter (= (get (session/get :current-detail)(session/get :works))))])
     ^{:key (session/get :current-detail)}[work-detail (filterv #(= (:title_short %) (session/get :current-detail)) (session/get :works) )]
     [:div])
   [:div.row
    ;; [ctg {:transitionName "example"}]
    (for [work (session/get :works)]
      ^{:key work} [work-splash work])]])

(defn about-sec []
  [:div
   [:p "I'm Jacob. I do research and development for " [:a {:href "http://www.tii.se"} "the Interactive Institute" ] " in Göteborg. This usually means I'm building AR and VR " [:a {:href "https://www.tii.se/people/jacob-michelsen#projects"} "prototypes"] ". I also write " [:a {:href "http://gup.ub.gu.se/publication/196447"} "papers"]"."]])

;; -------------------------
;; Routes
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
  (session/put! :params params)
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

(defn current-page []
  [(session/get :current-page) (session/get :params)])

(defn bs-main []
  [:div.container
   [:div.jumbotron
    [:div.row
     [:div.col-sm-12
     [:a {:href "#about"}
       [:h1 "R A K E T H O P P"]]]
     [:div.row]]
    [:div.row
     [current-page]]
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
  (re-frame/dispatch [:initialise-db])
  (re-frame/dispatch [:load-works])
  (reagent/render-component
   (fn [][bs-main])
   (.-body js/document)))

(init!)

;; History configuration from Secretary docs
(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))

(fw/start
 {
  
  ;; configure a websocket url if you are using your own server
  :websocket-url "ws://localhost:3500/figwheel-ws"

  ;; optional callback
  :on-jsload (fn [] (print "reloaded"))

  ;; The heads up display is enabled by default

  ;; :heads-up-display false

  ;; when the compiler emits warnings figwheel
  ;; blocks the loading of files.
  ;; To disable this behavior:
  ;; :load-warninged-code true

  ;; if figwheel is watching more than one build
  ;; it can be helpful to specify a build id for
  ;; the client to focus on
  ;; :build-id "example"
})
