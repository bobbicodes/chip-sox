#!/usr/bin/env bb

(defn notes [tempo]
  {:whole   (/ 240 tempo)
   :half    (/ 120 tempo)
   :quarter (/ 60 tempo)
   :qtrip   (/ 40 tempo)
   :eighth  (/ 30 tempo)
   :etrip   (/ 20 tempo)
   :teenth  (/ 15 tempo)
   :ttrip   (/ 10 tempo)})

; closed hi-hat - 42
; sox -n drum-h-1.wav synth 0.01 noise bass -5 fade 0 "$whole" "$whole" trim 0.0 "$whole"

(defn hi-hat [tempo]
  (let [whole (str (:whole (notes tempo)))]
    (shell/sh "sox" "-n"
              "samples/drum-42-whole.wav" 
              "synth" "0.01" "noise" 
              "bass" "-5" 
              "fade" "0" whole whole 
              "trim" "0.0" whole)))

(let [[tempo] *command-line-args*
      files   (str/split-lines (:out (shell/sh "ls")))]
  (when (empty? tempo)
    (println "Usage: <tempo>")
    (System/exit 1))
  (when-not (some #{"samples/drum-42-whole.wav"} files)
    (println (str "Generating sample: Closed hi-hat"))
    (hi-hat (Integer/parseInt tempo))))