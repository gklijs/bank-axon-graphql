(ns tech.gklijs.bank.views
  (:require [re-frame.core :as re-frame]
            [tech.gklijs.bank.subs :as subs]
            [tech.gklijs.bank.templates :as templates]))

(defn main-panel []
  [:div
   (let [selected-nav (re-frame/subscribe [::subs/nav])]
     (apply templates/nav-bar @selected-nav))
   [:div.section
    [:div.container
     [:div.columns
      [:div.column.is-one-quarter
       (let [show-left (re-frame/subscribe [::subs/show-left])]
         (when @show-left
           (let [left (re-frame/subscribe [::subs/left])]
             (apply templates/left-content @left))))]
      [:div.column.is-half
       (let [middle (re-frame/subscribe [::subs/middle])]
         (apply templates/middle-content @middle))]
      (let [selected-nav (re-frame/subscribe [::subs/selected-nav])]
          [:div.column.is-one-quarter
           (when (= :client @selected-nav)
             (let [login-status (re-frame/subscribe [::subs/login-status])]
               (templates/login @login-status)))
           (let [max-items (re-frame/subscribe [::subs/max-items])]
             (templates/max-items-buttons @max-items))
           (let [show-arguments (re-frame/subscribe [::subs/show-arguments])]
             (templates/show-argument-buttons @show-arguments))])]]]])
