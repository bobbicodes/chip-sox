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

(defn midi->freq [n]
  (* 440 (Math/pow 2 (/ (- n 69) 12))))

(defn create-note [wave pitch val len]
  (shell/sh "sox" "-n" 
            (str "note-" pitch "-" wave "-" val ".wav") 
            "synth" len wave
            (str (midi->freq (Integer/parseInt pitch))) 
            "trim" "0" len))

; Example usage:
; $ ./create-note.clj triangle 43 240
; Generating sample: 43-triangle

(let [[wave pitch tempo] *command-line-args*
      files (str/split-lines (:out (shell/sh "ls")))]
  (when (or (empty? wave) (empty? pitch) (empty? tempo))
    (println "Usage: <wave> <pitch> <tempo>")
    (System/exit 1))
  (when-not (some #{(str "note-" pitch "-" wave "-whole.wav")} files)
    (println (str "Generating sample: " pitch "-" wave))
    (doall (for [[val len] (notes (Integer/parseInt tempo))]
             (create-note wave pitch (name val) (str (float len)))))))

(System/exit 0)