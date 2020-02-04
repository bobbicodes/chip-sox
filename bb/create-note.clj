#!/usr/bin/env bb

(defn midi->freq [n]
  (* 440 (Math/pow 2 (/ (- n 69) 12))))

(defn create-note [wave pitch val len]
  (shell/sh "sox" "-n" 
            (str "note-" pitch "-" wave "-" val ".wav") 
            "synth" len wave 
            (str (midi->freq (Integer/parseInt pitch))) 
            "trim" "0" len))

(let [[wave pitch tempo] *command-line-args*
      whole (/ 240 (Integer/parseInt tempo))
      half (/ whole 2)
      quarter (/ half 2)
      qt (/ 40 (Integer/parseInt tempo))
      eighth (/ quarter 2)
      et (/ qt 2)
      teenth (/ eighth 2)
      tt (/ et 2)
      files (str/split-lines (:out (shell/sh "ls")))]
  (when (or (empty? wave) (empty? pitch) (empty? tempo))
    (println "Usage: <wave> <pitch> <tempo>")
    (System/exit 1))
  (when-not (some #{(str "note-" pitch "-" wave "-whole.wav")} files)
    (println (str "Generating sample: "  "note-" pitch "-" wave "-whole.wav"))
    (create-note wave pitch "whole" (str (float whole)))
    (create-note wave pitch "half" (str (float half)))
    (create-note wave pitch "quarter" (str (float quarter)))
    (create-note wave pitch "qt" (str (float qt)))
    (create-note wave pitch "eighth" (str (float eighth)))
    (create-note wave pitch "et" (str (float et)))
    (create-note wave pitch "teenth" (str (float teenth)))
    (create-note wave pitch "tt" (str (float tt)))))

(System/exit 0)