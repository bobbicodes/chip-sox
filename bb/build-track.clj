#!/usr/bin/env bb

(defn read-file [file]
  (with-open [rdr (io/reader file)]
    (doall (line-seq rdr))))

(defn init-wav []
  (shell/sh "sox" "-n" "-c" "1" "temp0.wav" "trim" "0.0" "0.0"))

(defn append-note [wave pitch val]
  (shell/sh "sox" "temp0.wav" 
            (str "note-" pitch "-" wave "-" val ".wav")
            "temp1.wav")
  (shell/sh "mv" "temp1.wav" "temp0.wav"))

; <wave> is one of sine, square, triangle, sawtooth, trapezium, exp, [white] noise, tpdfnoise, pinknoise, brownnoise, pluck

(let [[file wave tempo] *command-line-args*]
  (when (or (empty? file) (empty? wave) (empty? tempo))
    (println "Usage: <file> <wave> <tempo>")
    (System/exit 1))
  (shell/sh "rm" "*.wav")
  (init-wav)
  (doseq [[pitch val] (map #(str/split (str %) #" ") (read-file file))]
    (shell/sh "./create-note.clj" wave pitch tempo)
    (append-note wave pitch val)))