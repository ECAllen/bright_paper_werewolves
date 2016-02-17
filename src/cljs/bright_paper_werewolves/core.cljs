(ns bright-paper-werewolves.core
    (:require [reagent.core :as reagent]
              [reagent.ratom :as ratom]
              [reagent.debug :as debug]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ajax.core :as ajax]
              [markdown.core :refer [md->html]]
              [cognitect.transit :as transit]
              [dommy.core :refer-macros [sel sel1]]))


;; -------------------------
;; Globals

;; reagent vars for react
(def posts (reagent/atom {}))
; (def p (ratom/run! (debug/println "posts: " @posts)))

;; --------------------------
;; Testing

(def server "localhost")
(def port "3000")

;; --------------------------
;; Production

; (def server "ecallen.com")
; (def port "4010")

(def userid "ecallen")

;; --------------------------
;; Highlight



;; -------------------------
;;  AJAX

;; error handler for debugging
(defn error-handler [response]
  (debug/println (str "Error status: " (:status response)))
  (debug/println (str "Status details: " (:status-text response)))
  (debug/println (str "Failure: " (:failure response)))
  (if (contains? response :parse)
    (debug/println (str "Parse error: " (:parse response)))
    (debug/println (str "Original Text: " (:original-text response))))
  (debug/println (str "Error response: " response)))

(defn posts-handler [response]
  (reset! posts {})
  (swap! posts conj response))

(defn ajax-get [url handler error-handler]
  (ajax/GET url
    {:handler posts-handler
     :error-handler error-handler
     :response-format :transit}))

(defn get-posts []
  (ajax-get (str "http://" server ":" port "/posts/" userid) posts-handler error-handler))

;; -------------------------
;; Views

(defn highlight-code [html-node]
  (let [nodes (sel [:pre :code])]
    (doall (for [node nodes]
               (.highlightBlock js/hljs node)))))

(defn markdown-component [content]
           [(with-meta
              (fn []
                [:div {:dangerouslySetInnerHTML
                       {:__html (-> content md->html)}}])
              {:component-did-mount
                (fn [this]
                  (let [node (reagent/dom-node this)]
                    (highlight-code node)))})])


(defn home-page []
  [:div
   [:h1 "ECAllen"]
   (doall (for [k (keys (sort-by (comp :post-timestamp second) > @posts))]
           ^{:key k}
           [:section
             [:h2 (get-in @posts [k :title])]
             [:p (get-in @posts [k :post-timestamp])]
             [:p (markdown-component (get-in @posts [k :text]))]]))])


(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; Initialize app
(get-posts)

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
