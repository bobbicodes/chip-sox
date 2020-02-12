#!/usr/bin/env bb

(defn read-file [file]
  (with-open [rdr (io/reader file)]
    (doall (line-seq rdr))))

(defn init-wav []
  (shell/sh "sox" "-n" "-c" "1" "samples/temp0.wav" "trim" "0.0" "0.0"))

(defn append-note [wave pitch val]
  (shell/sh "sox" "samples/temp0.wav" 
            (str "samples/note-" pitch "-" wave "-" val ".wav")
            "samples/temp1.wav")
  (shell/sh "mv" "samples/temp1.wav" "samples/temp0.wav"))

; <wave> is one of sine, square, triangle, sawtooth, trapezium, exp, [white] noise, tpdfnoise, pinknoise, brownnoise, pluck

(let [[file wave tempo] *command-line-args*]
  (when (or (empty? file) (empty? wave) (empty? tempo))
    (println "Usage: <file> <wave> <tempo>")
    (System/exit 1))
  (init-wav)
  (println (str "Building track - " file))
  (doseq [[pitch val] (map #(str/split (str %) #" ") (read-file file))]
    (shell/sh "./create-note.clj" wave pitch tempo)
    (append-note wave pitch val))
  (shell/sh "mv" "samples/temp0.wav" (str file ".wav")))