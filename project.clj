(defproject nl.openweb/bank-axon-graphql "0.1.0-SNAPSHOT"
  :plugins [[lein-modules "0.3.11"]]
  :modules {:inherited
                      {:repositories  [["confluent" "https://packages.confluent.io/maven/"]]
                       :aliases       {"all" ^:displace ["do" "clean," "test," "install", "uberjar"]
                                       "-f"  ["with-profile" "+fast"]}
                       :scm           {:dir ".."}
                       :javac-options ["-target" "11" "-source" "11"]
                       :license       {:name "MIT License"
                                       :url  "https://opensource.org/licenses/MIT"
                                       :key  "mit"
                                       :year 2019}}
            :versions {ch.qos.logback/logback-classic                 "1.3.0-alpha5"
                       com.damballa/abracad                           "0.4.14-alpha2"
                       com.fasterxml.jackson.core/jackson-annotations "2.11.3"
                       com.fasterxml.jackson.core/jackson-core        "2.11.3"
                       com.fasterxml.jackson.core/jackson-databind    "2.11.3"
                       hikari-cp/hikari-cp                            "2.13.0"
                       io.confluent/kafka-avro-serializer             "6.0.0"
                       org.apache.avro/avro                           "1.9.1"
                       org.clojure/clojure                            "1.10.1"
                       org.clojure/data.json                          "0.2.6"
                       org.clojure/tools.logging                      "0.5.0"
                       org.postgresql/postgresql                      "42.2.18"
                       seancorfield/next.jdbc                         "1.1.613"}})
