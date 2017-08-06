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

(defn fibo
  ([]
   (fibo 10 [1 0]))
  ([t]
   (fibo t [1 0]))
  ([t fibr]
   (let [n (- (count fibr) 1)]
     (if (= t n)
       {:steps t :fibo-array fibr :fibo-result (first fibr)}
       (recur t (apply vector (+ (first fibr) (second fibr)) fibr))))))

(defn golden-spiral
  ([]
   (golden-spiral 10))
  ([t]
   (map (fn[x] {:x x :y x}) (-> (fibo t) :fibo-array))))

(s/defschema god
  {:name s/Str
   :symbol s/Str
   :major-cult-center s/Str
   :gender (s/enum :male :female)
   :offspring s/Str
   :greek-equivalent s/Str})

(s/defschema fibo-numbers
  {:steps Long, :fibo-array [Long], :fibo-result Long})

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
      (GET "/fibo" []
        :return fibo-numbers
        :query-params [steps :- Long]
        :summary "return fibonacci numbers"
        (ok (fibo steps)))
      (POST "/add-god" []
        :return god
        :body [gods god]
        (ok gods))))))

;;Middleware is a function that receives the request and response objects of an HTTP request/response cycle.
;;in other frameworks “middleware” is called “filters”
(defn wrap-log-request
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

(defn -main []
  (run-server (app) {:port (:port @config)})
  (println "Server running at port:" (:port @config)))
