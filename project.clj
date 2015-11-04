(defproject memtest "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [reagent "0.5.1"]]
  :plugins [[lein-cljsbuild "1.1.0"]]
  :hooks [leiningen.cljsbuild]
  :profiles {:prod {:cljsbuild
                    {:builds
                     {:client {:compiler
                               {:optimizations :advanced
                                :preamble ^:replace ["reagent/react.min.js"]
                                :output-dir "/home/steve/dev/scpike.com/site/memtest"
                                :output-to "/home/steve/dev/scpike.com/site/memtest/memtest.js"
                                :pretty-print false}}}}}
             :srcmap {:cljsbuild
                      {:builds
                       {:client {:compiler
                                 {:source-map "target/memtest.js.map"
                                  :source-map-path "memtest"}}}}}}
  :source-paths ["src"]
  :cljsbuild
  {:builds
   {:client {:source-paths ["src"]
             :compiler
             {:preamble ["reagent/react.js"]
              :output-dir "target/memtest"
              :output-to "target/memtest.js"
              :pretty-print true}}}
   :test-commands {"unit-tests" ["phantomjs" :runner
                                 ]}})
