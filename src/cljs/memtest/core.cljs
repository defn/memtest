(ns memtest.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [memtest.data :as mdata :refer [counter gameboard selected highlighted matched]]))

(def colors ; colors match the cell number
  {1 "#677685", 2 "#FFB492", 3 "#8EE6CA", 4 "#92387E",
   5 "#FFF6C9", 6 "#5C58EB", 7 "#D1052D", 8 "#857A67"})

; A gameboard is a grid of cells, each uniquely identified, but two cells will
; have the same number and colors.  The game is won when all cells have been
; matched

(defn won-game?
  []
  ; game is won when count of matches is equal to half of gameboard, because
  ; cells contain duplicate numbers
  (and (= (/ (count @gameboard) 2) (count @matched))
       (not= (count @matched) 0)))

(defn add-cell [n]
  ; add a numbered cell with a unique id
  (let [id (swap! counter inc)]
    (swap! gameboard assoc id {:id id :number n :color (colors n)})))

(defn new-game 
  []
  ; game starts out with an empty board, no cell selected, nothing hilighted,
  ; and no matches
  (reset! counter 0)
  (reset! gameboard (sorted-map))
  (reset! selected nil)
  (reset! matched #{})
  (reset! highlighted #{})
  ; take two sets of numbers (1..8) and randomize their order, then add them as
  ; cells
  (doseq [x (shuffle (into (range 1 9) (range 1 9)))]
    (add-cell x)))

(defn select-cell
  [cell]                   ; ensures one cell is colored via selection
  (reset! highlighted #{}) ; dont highlight anything
  (reset! selected cell))  ; mark cell as selected

(defn lose-cell
  [cell]                                 ; ensures two cells are colored via highlighting
  (reset! highlighted #{cell @selected}) ; highlight selected and current cell
  (reset! selected nil))                 ; dont select anything

(defn win-cell
  [{:keys [number]}]                     ; ensures two more cells are colored via match
  (reset! selected nil)                  ; dont select anything
  (reset! highlighted #{})               ; dont highlight anything
  (swap! matched conj number))           ; mark number as matched

(defn winning-click?
  [{:keys [number id]}]
  (and (= (:number @selected) number) ; win if the number matched the selected cell
       (not= (:id @selected) id)))    ; and if it's not the same selected cell

(defn handle-click
  [{:keys [number id] :as cell}]
  (cond
   (= @selected cell) (reset! selected nil) ; reset if selected cell is selected again
   (nil? @selected) (select-cell cell)      ; set as selected if nothing was selected
   (winning-click? cell) (win-cell cell)    ; mark as won if click is a dinner
   :else (lose-cell cell)))                 ; else mark as lost

(defn highlighted?
  [cell]
  (or (get @matched (:number cell)) ; color if number matched
      (= @selected cell)            ;       if selected
      (get @highlighted cell)))     ;       if highlighted

(defn board-cell []
  (fn [{:keys [number color id] :as cell}]
    ; display cell with background color
    [:td {:class "game-cell"
          :style (if (highlighted? cell) {:background-color color} {})
          :on-click #(handle-click cell)}]))

(defn board-row []
  (fn [x]
    [:tr
     ; loop through each cell in a row
     (for [{:keys [id] :as cell} x]
       ^{:key id} [board-cell cell])]))

;; -------------------------
;; Views

(defn board-page []
  (fn []
    (let [cells (vals @gameboard)]
      [:div#container
       ; game title
       [:h1 "The Memory Game"]
       ; win status
       [:h2 (if (won-game?) "You won!!!")]
       ; the gameboard 
       [:table#gameboard
        ; taking 4 cells at a time for each row
        (map-indexed
          (fn [idx x] ^{:key idx} [board-row x])
          (partition 4 cells))]
       ; text link to reset the game
       [:p [:a {:class "new-game" :on-click #(new-game)
                :href "#"} "New Game"]]])))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'board-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [board-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (new-game)
  (mount-root))
