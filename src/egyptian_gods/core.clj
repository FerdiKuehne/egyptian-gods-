(ns egyptian-gods.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [pdfboxing.text :as text]
            [pdfboxing.info :as info]
            [ring.util.response :as resp]
            [clojure.string :as str]
            [schema.core :as s]
            [ring.swagger.schema :as rs]
            [clj-time.local :as l]))

(def config (atom {:port 3000
                   :pdf "egyptgods.pdf"
                   :list-of-gods (text/extract "egyptgods.pdf")}))

(s/defschema god
  {:name s/Str
   :symbol s/Str
   :major-cult-center s/Str
   :gender (s/enum :male :female)
   :offspring s/Str
   :greek-equivalent s/Str})

(defn mapify-gods
  [pdf]
  (map
   (fn [x]
     (let [y (str/split x #"info: ")]
       {:name (str/trim (str/replace (get y 0) "name:" ""))
        :info (get y 1)}))
   (str/split pdf #"\n")))

(defn timelord []
  (l/format-local-time (l/local-now) :basic-date-time))

(def app
  (api
    {:swagger
     {:ui   "/"
      :spec "/swagger.json"
      :data {:info {:title "Egyptian Gods"}
             :tags [{:name "api"}]}}}
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
      (ok "I am a God")))))


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
