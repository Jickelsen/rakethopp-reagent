(ns rakethopp-reagent.session
  (:refer-clojure :exclude [get])
  (:require [reagent.core :as reagent :refer [atom]]))

(def state (atom {:games [] :current-page false}))

(defn get [k & [default]]
  (clojure.core/get @state k default))

(defn put! [k v]
  (swap! state assoc k v))

(defn update-in! [ks f & args]
  (clojure.core/swap!
   state
   #(apply (partial update-in % ks f) args)))
