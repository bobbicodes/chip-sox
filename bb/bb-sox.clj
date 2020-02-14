(def tempo "175")

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
            (str "samples/note-" pitch "-" wave "-" val ".wav")
            "synth" len wave
            (str (midi->freq (Integer/parseInt pitch)))
            "trim" "0" len))

(defn play [file]
  (shell/sh "play" file))

(defn create-notes
  "<wave> is one of:
  sine, square, triangle, sawtooth, trapezium, exp,
  [white] noise, tpdfnoise, pinknoise, brownnoise, pluck"
  [wave pitch]
  (let [files (str/split-lines (:out (shell/sh "ls")))]
    (when-not (some #{(str "samples/note-" pitch "-" wave "-whole.wav")} files)
      (doseq [[val len] (notes (Integer/parseInt tempo))]
        (create-note wave pitch (name val) (str (float len)))))))

(comment

  (create-notes "triangle" "42")
  
  (play "samples/note-42-triangle-whole.wav")

  )