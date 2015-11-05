(ns ^:figwheel-no-load memtest.data
    (:require [reagent.core :as reagent :refer [atom]]))

(def counter (atom 0))              ; generates unique ids for each cell
(def gameboard (atom (sorted-map))) ; gameboard is sorted to preserve cell order
(def matched (atom #{}))            ; numbers that have been matched
(def selected (atom nil))           ; cell that was last selected
(def highlighted (atom #{}))        ; cells that are highlighted
