(defproject memtest "0.1.0-SNAPSHOT"
            :description "memtest game"

            :dependencies [[org.clojure/clojure "1.7.0"]
                           [cljsjs/react-bootstrap "0.25.2-0"]
                           [re-frame "0.4.1"]
                           [reagent "0.5.1"]
                           [reagent-forms "0.5.13"]
                           [reagent-utils "0.1.5"]
                           [ring-server "0.4.0"]
                           [ring "1.4.0"]
                           [ring/ring-defaults "0.1.5"]
                           [prone "0.8.2"]
                           [compojure "1.4.0"]
                           [hiccup "1.0.5"]
                           [environ "1.0.1"]
                           [org.clojure/clojurescript "1.7.145" :scope "provided"]
                           [secretary "1.2.3"]
                           [venantius/accountant "0.1.4"]
                           [devcards "0.2.0-8"] 
                           ]

            :plugins [[lein-environ "1.0.1"]
                      [lein-asset-minifier "0.2.2"]]

            :ring {:handler memtest.handler/app}

            :main memtest.server

            :clean-targets ^{:protect false} [:target-path
                                              [:cljsbuild :builds :app :compiler :output-dir]
                                              [:cljsbuild :builds :app :compiler :output-to]]

            :source-paths ["src/clj" "src/cljc"]

            :minify-assets {
                            :assets {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

            :cljsbuild {:builds {:app {:source-paths ["src/cljs" "src/cljc"]
                                       :compiler {:output-to "resources/public/js/app.js"
                                                  :output-dir "resources/public/js/out"
                                                  :asset-path "js/out"
                                                  :optimizations :none
                                                  :pretty-print true}}}}

            :profiles {:dev {:repl-options {:init-ns memtest.repl}

                             :dependencies [[ring/ring-mock "0.3.0"]
                                            [ring/ring-devel "1.4.0"]
                                            [lein-figwheel "0.4.1"]
                                            [org.clojure/tools.nrepl "0.2.11"]
                                            [com.cemerick/piggieback "0.1.5"]
                                            [pjstadig/humane-test-output "0.7.0"]]

                             :source-paths ["env/dev/clj"]
                             :plugins [[lein-figwheel "0.4.1"]
                                       [lein-cljsbuild "1.1.0"]]
                             :hooks [minify-assets.plugin/hooks]

                             :injections [(require 'pjstadig.humane-test-output)
                                          (pjstadig.humane-test-output/activate!)]

                             :figwheel {:http-server-root "public"
                                        :server-port 3449
                                        :nrepl-port 7002
                                        :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"
                                                           ]
                                        :css-dirs ["resources/public/css"]
                                        :ring-handler memtest.handler/app}

                             :env {:dev true}

                             :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                                        :compiler {:main "memtest.dev"
                                                                   :source-map true}}

                                                  :devcards {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                                                             :figwheel {:devcards true}
                                                             :compiler {:main "memtest.cards"
                                                                        :asset-path "js/devcards_out"
                                                                        :output-to "resources/public/js/app_devcards.js"
                                                                        :output-dir "resources/public/js/devcards_out"
                                                                        :source-map-timestamp true}}}}}})
