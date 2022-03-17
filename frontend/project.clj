(defproject tech.gklijs/bank "0.1.0-SNAPSHOT"
  :description "front-end for the kafka workshop"
  :url "https://github.com/gklijs/bank-axon-graphql/tree/master/frontendd"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.773" :exclusions [com.google.errorprone/error_prone_annotations com.google.code.findbugs/jsr305]]
                 [reagent "1.0.0"]
                 [re-frame "1.1.2"]
                 [re-graph "0.1.15" :exclusions [args4j]]
                 [bidi "2.1.6"]
                 [kibu/pushy "0.3.8"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-sass "0.5.0"]]
  :min-lein-version "2.5.3"
  :source-paths ["src/clj"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "resources/public/css"]
  :figwheel {:css-dirs ["resources/public/css"]}
  :sass {:src              "resources/app/stylesheets"
         :output-directory "resources/public/css"
         :source-maps      false
         :command          :sassc}
  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.2"]
                   [day8.re-frame/re-frame-10x "0.7.0"]]
    :plugins      [[lein-figwheel "0.5.20"]]}}
  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "tech.gklijs.bank.core/mount-root"}
     :compiler     {:main                 tech.gklijs.bank.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :optimizations        :none
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           day8.re-frame-10x.preload]
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                    :external-config      {:devtools/config {:features-to-install :all}}}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            tech.gklijs.bank.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
