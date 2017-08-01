(ns egyptian-gods.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer :all]
            [ring.util.response :as resp] ))

(defroutes app-routes
  (GET "/" [] (resp/file-response "index.html" {:root "resources/public"}))
  (GET "/list-of-gods" [] (resp/file-response "index.html" {:root "resources/public"}))
  (route/resources "/"))

(defn -main []
  (run-server app-routes {:port 3000}))


