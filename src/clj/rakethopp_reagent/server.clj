(ns rakethopp-reagent.server
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as sql]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [response resource-response content-type]]
            [cemerick.drawbridge :as drawbridge]
            [environ.core :refer [env]]))

(defn- authenticated? [user pass]
  ;; TODO: heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/rakethopp"))

(defn json-request [& [request]]
  (let [query (:work-type request)]
    (println request)
    (cond
      (= query "games")
      (response (sql/query spec ["select * from games order by num desc"]) )
      (= query "projects")
      (response (sql/query spec ["select * from projects order by num desc"]) )
      :else
      (response (sql/query spec ["select * from projects order by num desc"]) )
      )))

(defn fix-map [a-map] (into {}
                    (for [[k v] a-map]
                      [(keyword k) v])))

(defroutes app
  (route/resources "/")
  (ANY "/repl" {:as req}
       (drawbridge req))
  (GET "/" [] (-> (resource-response "index.html" {:root "public"}) (content-type "text/html")))
  (POST "/api" req
       ;; (response {:foo "bar"})
        (json-request (fix-map(:body req)))
       )
  ;; (ANY "*" []
  ;;      (route/not-found (slurp (io/resource "404.html"))))
  )

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn wrap-app [app]
  ;; TODO: heroku config:add SESSION_SECRET=$RANDOM_16_CHARS
  (let [store (cookie/cookie-store {:key (env :session-secret)})]
    (-> app
        ((if (env :production)
           wrap-error-page
           trace/wrap-stacktrace))
        (middleware/wrap-json-body {:keywords? true :bigdecimals? true})
        (middleware/wrap-json-response)
        ;; (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
        (site {:session {:store store}}))))

(defn run-local [& [port]] ((-> #'app
        ;; (middleware/wrap-json-body)
        ;; (middleware/wrap-json-response)
        )))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
