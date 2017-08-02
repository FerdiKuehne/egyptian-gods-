(defproject egyptian-gods "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [pdfboxing "0.1.13"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.1"]
                 [metosin/compojure-api "1.1.10"]
                 [clj-time "0.14.0"]]
  :ring {:handler egyptian-gods.core/app}
  :profiles {:dev
             {:plugins [[lein-ring "0.12.0"]]}})
