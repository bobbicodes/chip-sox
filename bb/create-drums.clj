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

(defn kick-sine [val len]
  (shell/sh "sox" "-n"
            (str "samples/drum-36-sine-" val ".wav")
            "trim" "0.0" len))

(defn kick-noise [val len]
  (shell/sh "sox" "-n"
            (str "samples/drum-36-noise-" val ".wav")
            "synth" "0.03" "noise"
            "treble" "-15"
            "fade" "0" len len
            "trim" "0.0" len))

(defn kick-mix [val]
  (let [sine  (str "samples/drum-36-sine-" val ".wav")
        noise (str "samples/drum-36-noise-" val ".wav")]
    (shell/sh "sox" "-m"
              sine noise
              (str "samples/drum-36-" val ".wav")
              "gain" "5"
              "treble" "-15")
    (shell/sh "rm" sine noise)))

(defn hi-hat [val len]
  (shell/sh "sox" "-n"
            (str "samples/drum-42-" val ".wav")
            "synth" "0.01" "noise"
            "bass" "-5"
            "fade" "0" len len
            "trim" "0.0" len))

(defn snare [val len]
  (shell/sh "sox" "-n"
            (str "samples/drum-38-" val ".wav")
            "synth" "0.1" "noise"
            "fade" "0" len len
            "trim" "0.0" len))

(let [[tempo] *command-line-args*
      files   (str/split-lines (:out (shell/sh "ls")))]
  (when (empty? tempo)
    (println "Usage: <tempo>")
    (System/exit 1))
  (when-not (some #{"samples/drum-42-whole.wav"} files)
    (doseq [[val len] (notes (Integer/parseInt tempo))]
      (kick-noise (name val) (str (float len)))
      (kick-sine (name val) (str (float len)))
      (kick-mix (name val))
      (hi-hat (name val) (str (float len)))
      (snare (name val) (str (float len))))))