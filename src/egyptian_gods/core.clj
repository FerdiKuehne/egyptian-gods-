(ns egyptian-gods.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer :all]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer :all]
            [compojure.route :as route]
            [pdfboxing.text :as text]
            [pdfboxing.info :as info]
            [clojure.string :as str]))

(def port {:port 3000})

(def pdf "egyptgods.pdf")

(def listofGods (text/extract pdf))

listofGods


(map #(vector %) (str/split listofGods #"\n"))

(def mapifygods
  (map
   (fn [x]
     (let [y (str/split x #"info: ")]
       {:name (str/trim (str/replace (get y 0) "name:" ""))
        :info (get y 1)}))
   (str/split listofGods #"\n")))



(defroutes app-routes
  (GET "/" [] (resp/file-response "index.html" {:root "resources/public"}))
  (GET "/list-of-gods" [] (resp/file-response "list.html" {:root "resources/public"}))
  (GET "/app-version" req (str "Hello World v" (:app-version req)))
  (GET "/pdf" [] mapifygods)
    (route/resources "/"))

(defn wrap-version [handler]
  (fn [request]
      (handler (assoc request :app-version "1.0.1"))))

(defn -main []
  (run-server app-routes port)
  (println "server online at port:" (-> port :port)))
