(ns bright-paper-werewolves.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]))

(def mount-target
  [:div#app
   [:h3 "loading..."]])

(def loading-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css
      (if (env :dev) "css/tufte.css" "css/tufte.min.css")
      "//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.1.0/styles/zenburn.min.css"
      "css/site.css")]
    [:body
     mount-target
     (include-js
      "//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.1.0/highlight.min.js"
      "js/app.js")]]))

(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults #'routes site-defaults)]
    (if (env :dev) (-> handler wrap-exceptions wrap-reload) handler)))
