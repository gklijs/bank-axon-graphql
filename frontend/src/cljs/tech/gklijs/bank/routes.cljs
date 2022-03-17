(ns tech.gklijs.bank.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]
            [tech.gklijs.bank.events :as events]))

(def routes ["/" {""                                  :home
                  "employee"                          :bank-employee
                  "client"                            :client}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (let [handler (:handler matched-route)]
    (re-frame/dispatch [::events/set-selected-nav handler])))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))
