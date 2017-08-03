 (ns egyptian-gods.core
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as cr]
            [org.httpkit.server :refer [run-server]]
            [pdfboxing.text :as text]
            [pdfboxing.info :as info]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [schema.core :as s]
            [ring.util.response :as resp]
            [ring.util.http-response :refer :all]
            [ring.swagger.schema :as rs]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [clj-time.local :as l]))

(def config (atom {:port 3000
                   :pdf "egyptgods.pdf"
                   :list-of-gods (text/extract "egyptgods.pdf")}))

(defn response-template [status data]
  (ok {:status status :data data}))

(s/defschema god
  {:name s/Str
   :symbol s/Str
   :major-cult-center s/Str
   :gender (s/enum :male :female)
   :offspring s/Str
   :greek-equivalent s/Str})

(defn fiboo
  ([]
   (let [fib [0 1 1]]
     {:info "Fibonacci number" :fib-r fib :result (last fib)}))
  ([n]
   (let [fib [0 1]]
     {:info "fibo after " n "steps " :fib-r fib :result (last fib)})))

(defn mapify-gods
  [pdf]
  (map
   (fn [x]
     (let [y (str/split x #"info: ")]
       {:name (str/trim (str/replace (get y 0) "name:" ""))
        :info (get y 1)}))
   (str/split pdf #"\n")))

(defn timelord []
  "return local time"
  (l/format-local-time (l/local-now) :basic-date-time))

(defn create-routes []
  "function that created endpoints and resources"
  (routes
   (api
    {:swagger {:ui   "/swagger"
               :spec "/swagger.json"
               :data {:info {:title "Egyptian Gods"}
                      :tags [{:name "api"}]}}}
    (undocumented
     (cr/resources "/")
     ;;(cr/not-found (response-template :failure :not_found))
     (GET "/" [] ;;entry to start page
       (io/resource "public/index.html")))
    (context "/api" []
      :tags ["api"]
      (GET "/plus" []
        :return {:result Long}
        :query-params [x :- Long, y :- Long]
        :summary "adds two numbers together"
        (ok {:result (+ x y)}))
      (GET "/pdf-list" []
        (ok (mapify-gods (-> @config :list-of-gods))))
      (GET "/server-name" []
        (ok (str "<h1>I am a Server </h1>")))
      (GET "/time-lord" []
        :return String
        :summary "return local time"
        (ok (timelord)))
      (POST "/add-god" []
        :return String
        :body [gods god]
        (ok "I am a God"))))))


;;Middleware is a function that receives the request and response objects of an HTTP request/response cycle.
;;in other frameworks “middleware” is called “filters”
(defn wrap-log-request
  ""
  [handler]
  (fn [req]
    (let [resp (handler req)]
      resp)))

(defn app [] 
  (-> (create-routes)
      wrap-log-request
      wrap-json-response
      wrap-multipart-params
      (wrap-cors :access-control-allow-origin [#"http://localhost:3000"]
                  :access-control-allow-methods [:get :put :post :delete])))

(run-server (app) {:port (:port @config)})


#_(defnroutes app-routes
   (GET "/" req
     (resp/file-response "index.html" {:root "resources/public"}))
   (GET "/list-of-gods" req
     (resp/file-response "list.html" {:root "resources/public"}))
   (GET "/app-version" req
     (str "Hello World v" (:app-version req)))
   (GET "/pdf" req
     (mapify-gods (-> @config :list-of-gods)))
   (route/resources "/")
   (route/not-found
    (resp/response {:message "Page not found 404"})))

;;handler function to handle all incoming requests 
