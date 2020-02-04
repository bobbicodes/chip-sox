#!/usr/bin/env bb

(defn midi->freq [n]
  (* 440 (Math/pow 2 (/ (- n 69) 12))))

(let [[wave pitch tempo] *command-line-args*
      whole (str (/ 240 (Integer/parseInt tempo)))
      files (str/split-lines (:out (shell/sh "ls")))]
  (when (or (empty? wave) (empty? pitch) (empty? tempo))
    (println "Usage: <wave> <pitch> <tempo>")
    (System/exit 1))
  (when-not (some #{(str "note-" pitch "-tri-1.wav")} files)
    (println (str "Generating sample: "  "note-" pitch "-tri-1.wav"))
    (shell/sh "sox" "-n" (str "note-" pitch "-tri-1.wav") "synth" whole wave (str (midi->freq (Integer/parseInt pitch))) "trim" "0" whole)))

(System/exit 0)