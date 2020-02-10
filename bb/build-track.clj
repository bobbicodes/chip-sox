#!/usr/bin/env bb

(defn read-file [file]
  (with-open [rdr (io/reader file)]
    (doall (line-seq rdr))))

(defn init-wav []
  (shell/sh "sox" "-n" "-c" "1" "temp0.wav" "trim" "0.0" "0.0"))
