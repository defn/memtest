(ns memtest.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def gameboard (atom (sorted-map)))
(def counter (atom 0))
(def found (atom #{}))
(def selected (atom nil))
(def highlighted (atom #{}))
(def colors
  {1 "#677685", 2 "#FFB492", 3 "#8EE6CA", 4 "#92387E",
   5 "#FFF6C9", 6 "#5C58EB", 7 "#D1052D", 8 "#857A67"})

(defn won-game?
  []
  (= (/ (count @gameboard) 2) (count @found)))

(defn add-piece [n]
  (let [id (swap! counter inc)]
    (swap! gameboard assoc id {:id id :number n :color (colors n)})))

(defn new-game
  []
  (reset! gameboard (sorted-map))
  (reset! selected nil)
  (reset! found #{})
  (reset! highlighted #{})
  (doseq [x (shuffle (into (range 1 9) (range 1 9)))]
    (add-piece x)))

(new-game)

(defn select-piece
  [piece]
  (reset! highlighted #{})
  (reset! selected piece))

(defn lose-piece
  [piece]
  (reset! highlighted #{piece @selected})
  (reset! selected nil))

(defn win-piece
  [{:keys [number]}]
  (reset! selected nil)
  (reset! highlighted #{})
  (swap! found conj number))

(defn winning-click?
  [{:keys [number id]}]
  (and (= (:number @selected) number)
       (not= (:id @selected) id)))

(defn handle-click
  [{:keys [number id] :as piece}]
  (cond
   (= @selected piece) (reset! selected nil)
   (nil? @selected) (select-piece piece)
   (winning-click? piece) (win-piece piece)
   :else (lose-piece piece)))

(defn highlighted?
  [piece]
  (or (get @found (:number piece))
      (= @selected piece)
      (get @highlighted piece)))

(defn board-cell []
  (fn [{:keys [number color id] :as piece}]
    [:td {:class "game-piece"
          :style (if (highlighted? piece) {:background-color color} {})
          :on-click #(handle-click piece)}]))

(defn board-row []
  (fn [x]
    [:tr
     (for [{:keys [id] :as piece} x]
       ^{:key id} [board-cell piece])]))

(defn board [props]
  (fn []
    (let [items (vals @gameboard)]
      [:div#container
       [:h1 "The memory game"]
       [:h2 (if (won-game?) "You won!")]
       [:table#gameboard
        (map-indexed
         (fn [idx x] ^{:key idx} [board-row x])
         (partition 4 items))]
       [:p [:a {:class "new-game" :on-click #(new-game)
                :href "#"} "New game"]]])))

(defn ^:export run []
  (reagent/render-component [board]
                            (.-body js/document)))
