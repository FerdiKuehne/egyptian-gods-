(defproject egyptian-gods "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [pdfboxing "0.1.13"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.1"]
                 [ring-cors "0.1.8"]
                 [metosin/compojure-api "1.1.10"]
                 [clj-time "0.14.0"]
                 [ring "1.5.0"]
                 [ring/ring-json "0.4.0"]
                 [http-kit "2.2.0"]
                 [compojure "1.5.1"]]
  :main ^:skip-aot egyptian-gods.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
