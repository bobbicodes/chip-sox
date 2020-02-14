(defn duration
  "Takes a note value as the denominator of its fraction of a whole,
  e.g. 1 = whole, 2 = half, 4 = quarter, 6 = quarter-note-tuplets, etc,
  and the song tempo in quarter-notes-per-minute.
  Returns the note's length in seconds."
  [val tempo]
  (/ (/ 240.0 val) tempo))

(defn midi->freq
  "Takes a MIDI number (0-127) representing a note's pitch,
  outputs its frequency in Hz (cycles per second). Middle C is 60."
  [n]
  (* 440.0 (Math/pow 2 (/ (- n 69) 12))))

(defn init-wav!
  "Creates an empty .wav file to append notes to."
  []
  (shell/sh "sox" "-n" "-c" "1" "samples/temp0.wav" "trim" "0.0" "0.0"))

(defn create-note!
  "Generates one of the following waveforms:
  sine, square, triangle, sawtooth, trapezium, exp,
  [white] noise, tpdfnoise, pinknoise, brownnoise, pluck."
  [wave pitch val tempo]
  (shell/sh "mkdir" "-p" "samples")
  (shell/sh "sox" "-n"
            (str "samples/" wave "-" pitch "-" val "-" tempo ".wav")
            "synth" (str (duration val tempo)) wave
            (str (midi->freq pitch))
            "trim" "0.0" (str (duration val tempo))))

(defn append-note!
  "Extends current track by concatenating a note to the end."
  [wave pitch val tempo]
  (shell/sh "sox" "samples/temp0.wav" 
            (str "samples/" wave "-" pitch "-" val "-" tempo ".wav")
            "samples/temp1.wav")
  (shell/sh "mv" "samples/temp1.wav" "samples/temp0.wav"))

(defn play! [file]
  (shell/sh "play" (str file ".wav")))

(defn sample-exists? [wave pitch val tempo]
  (some #{(str  "samples/" wave "-" pitch "-" val "-" tempo ".wav")}
        (str/split-lines (:out (shell/sh "ls")))))

(defn build-track! [file wave tempo notes]
  (init-wav!)
  (println (str "Building track - " file))
  (doseq [[pitch val] notes]
    (when-not (sample-exists? wave pitch val tempo)
      (create-note! wave pitch val tempo))
    (append-note! wave pitch val tempo))
  (shell/sh "mv" "samples/temp0.wav" (str file ".wav")))

;;;;;;;;;; Triangle bass/drums

(def tri-1
  {:deg-1 [[50 64] [46 64] [42 64] [40 64] [36 8] [0 32]]
   :deg-5 [[58 64] [55 64] [52 64] [42 64] [43 8] [0 32]]
   :deg-8 [[60 64] [48 8] [0 32]]})

(def tri-2
  {:deg-8 [[60 64] [48 24] [58 64] [55 64] [52 64] [42 64] [48 64] [0 32]]})

(def intro
  (concat (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-1)
          (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-2)
          (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-2)
          (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-2)))

(def verse-1
  (apply concat (repeat 4 (concat (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-1)))))

(comment
  (str (duration 16 175))
  (create-note! "triangle" 50 16 150)

  (build-track! "intro" "triangle" 170
                (concat intro verse-1))

  (play! "intro")

  )
