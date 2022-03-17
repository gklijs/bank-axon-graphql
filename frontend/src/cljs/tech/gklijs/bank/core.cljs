(ns tech.gklijs.bank.core
  (:require [reagent.dom :as rd]
            [re-frame.core :as re-frame]
            [re-graph.core :as re-graph]
            [tech.gklijs.bank.config :as config]
            [tech.gklijs.bank.db :refer [default-db]]
            [tech.gklijs.bank.transactions :refer [get-dispatches]]
            [tech.gklijs.bank.events :as events]
            [tech.gklijs.bank.routes :as routes]
            [tech.gklijs.bank.views :as views]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (rd/render [views/main-panel]
             (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch [::re-graph/init {:ws   {:url                  "ws://localhost:8888/subscriptions"
                                              :supported-operations #{:subscribe}}
                                       :http {:url "http://localhost:8888/graphql"}}])
  (doseq [dispatch (get-dispatches default-db)] (re-frame/dispatch dispatch))
  (dev-setup)
  (mount-root))
