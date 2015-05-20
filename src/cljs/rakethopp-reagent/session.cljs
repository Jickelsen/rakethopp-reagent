(ns rakethopp-reagent.session
  (:refer-clojure :exclude [get])
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [POST]]))

(def state (atom {}))

(defn get [k & [default]]
  (clojure.core/get @state k default))

(defn put! [k v]
  (swap! state assoc k v))

(defn update-in! [ks f & args]
  (swap!
   state
   #(apply (partial update-in % ks f) args)))

(defn ajax-put! [& {:keys [url work-type] :or {url "/php/getworks.php" work-type "game-detail"}}]
  (POST url {:params {:workType work-type}
             :format :raw
             :response-format :json
             :handler #(put! :works %)
             :keywords? true}))
