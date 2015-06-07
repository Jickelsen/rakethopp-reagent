(ns rakethopp-reagent.core
  (:require [clojure.string :as string]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as re-frame]
            [cljs-http.client :as http]
            ;; [ajax.core :as ajax]
            [secretary.core :as secretary :refer [dispatch!]]
            [secretary.core :include-macros true :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])

  (:require-macros [reagent.ratom  :refer [reaction]]
                   [cljs.core.async.macros :refer [go alt!]])
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
    :projects []

    ;; Sessions here instead of in reagent/session
    :current-page nil
    :params {}
    }))

(re-frame/register-sub
 :current-page
 (fn [db]
   (reaction (:current-page @db))))

(re-frame/register-handler
  :current-page-update
  (fn
    [app-state [_ current-page]]
    (assoc app-state :current-page current-page)))

(re-frame/register-sub
 :params
 (fn [db]
   (reaction (:params @db))))

(re-frame/register-handler
  :params-update
  (fn
    [app-state [_ params]]
    (println params)
    (assoc app-state :params params)))

(defn request-json
  [url k]
  (let [c (chan)]
    (go (let [response (<! (http/post url {:json-params {:work-type k}}))]
          ;;   (go (let [response (<! (ajax/POST url {:params {:workType k}
          ;;                     :format :raw
          ;;                     :response-format :json
          ;;                     :handler #(re-frame/dispatch [:process-works-response %1 k])
          ;;                     :error-handler #(re-frame/dispatch [:process-works-bad-response %1 k])
          ;;                     :keywords? true}))]
          (>! c response))
        )c))

(re-frame/register-handler
 :load-works
 (fn
   [app-state _]
   (go (while true
         (let [games-data (:body (<! (request-json "/api" :games)))
               projects-data (:body (<! (request-json "/api" :projects)))
               ]
           (re-frame/dispatch [:process-works-response games-data :games])
           (re-frame/dispatch [:process-works-response projects-data :projects])
           )
         (<! (timeout 30000))))
   app-state))

(re-frame/register-handler
  :process-works-response
  (fn
    [app-state [_ response k]]
    (assoc-in app-state [k] response)))

(re-frame/register-handler
 :process-works-bad-response
 (fn
   [app-state [_ response k]]
   (println "Error getting" k response)
   app-state))

(re-frame/register-sub
 :games
 (fn [db]
   (reaction (:games @db))))

(re-frame/register-sub
 :projects
 (fn [db]
   (reaction (:projects @db))))

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
     ^{:key x} [:div.col-sm-3.col-xs-6
      [:a.thumbnail {:data-lightbox ((first work) :title_short) :href (str "/img/" ((first work) :title_short) "_" x ".jpg")}
       ;;         (.error js/console (str "hello" x))
       [:img {:src (str "/img/" ((first work) :title_short) "_" x ".jpg")}]]])])

(defn work-splash [work work-type]
  [:div.col-sm-3.col-xs-6
   [:a.thumbnail {:href (str "#" work-type "/" (work :title_short))}
    [:img {:src (str "/img/" (work :title_short) "_th.jpg")}]
    [:div.caption
     [:h6 (work :title)]]]])

(defn works-sec [{work-id :work-id work-type :work-type}]
  ;; (println "work type is " work-type)
  (if-not (= "about" work-type)
    (let [works (cond
                  (=  work-type "games")
                  (re-frame/subscribe [:games])
                  (= work-type "projects")
                  (re-frame/subscribe [:projects]))
          ]
      [:div.col-sm-12
       ;; (println "work id is " work-id)
       ;; [ctg {:transitionName "example" :transitionLeave false}]
       (if-not (empty? work-id) 
         (let [selected-work (filterv #(= (:title_short %) work-id) @works)]
           (if-not (empty? selected-work)
             [work-detail selected-work])))
       [:div]
       [:div.row
        ;; [ctg {:transitionName "example"}]
        (for [work @works]
          ^{:key work} [work-splash work work-type])]])))

(defn about-sec []
  [:div
   [:p "I'm Jacob. I do research and development for " [:a {:href "http://www.tii.se"} "the Interactive Institute" ] " in Göteborg. This usually means I'm building AR and VR " [:a {:href "https://www.tii.se/people/jacob-michelsen#projects"} "prototypes"] ". I also write " [:a {:href "http://gup.ub.gu.se/publication/196447"} "papers"]"."]])

;; -------------------------
;; Routes
(defroute "/" []
  (re-frame/dispatch [:current-page-update empty-sec]))

(defroute "/:work-type" {:as params} 
  (re-frame/dispatch [:params-update params])
  (if (= (:work-type params) "about")
    (re-frame/dispatch [:current-page-update about-sec])
    (re-frame/dispatch [:current-page-update works-sec])
    )
  )

(defroute "/:work-type/:work-id" {:as params}
  (re-frame/dispatch [:params-update params])
  (re-frame/dispatch [:current-page-update works-sec])
  )

(defn current-page []
  (let [current-page (re-frame/subscribe [:current-page])
        params (re-frame/subscribe [:params])]
    (if (= @current-page nil)
      [empty-sec]
      [@current-page @params])))

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
  ;; (re-frame/dispatch [:current-page-update] empty-sec)
  (re-frame/dispatch [:initialise-db])
  (re-frame/dispatch [:load-works])
  (re-frame/dispatch [:current-page-update empty-sec])
  (reagent/render-component
   (fn [][bs-main])
   (.-body js/document)))

;; (init!)

;; History configuration from Secretary docs
(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))


