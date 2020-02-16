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
  ([wave pitch val tempo]
   (shell/sh "mkdir" "-p" "samples")
   (shell/sh "sox" "-n"
             (str "samples/" wave "-" pitch "-" val "-" tempo ".wav")
             "synth" (str (duration val tempo)) wave
             (str (midi->freq pitch))
             "fade" "t" "0.0015" (str (- (duration val tempo) 0.0015))
             "trim" "0.0" (str (duration val tempo))))
  ([wave pitch val tempo decay]
   (shell/sh "mkdir" "-p" "samples")
   (shell/sh "sox" "-n"
             (str "samples/" wave "-" pitch "-" val "-" tempo ".wav")
             "synth" (str (duration val tempo)) wave
             (str (midi->freq pitch))
             "fade" "t" "0.0015" (str decay) (str (- (duration val tempo) 0.0015))
             "trim" "0.0" (str (duration val tempo)))))

  (defn append-note!
    "Extends current track by concatenating a note to the end."
    [wave pitch val tempo]
    (shell/sh "sox" "samples/temp0.wav" 
              (str "samples/" wave "-" pitch "-" val "-" tempo ".wav")
              "samples/temp1.wav")
    (shell/sh "mv" "samples/temp1.wav" "samples/temp0.wav"))

  (defn concat-wav! [f1 f2 out]
    (shell/sh "sox" (str f1 ".wav") (str f2 ".wav") (str out ".wav"))) 

  (defn high-pass! [in out level]
    (shell/sh "sox" (str in ".wav") (str out ".wav") "flanger"
              "gain" "-20" "bass" "-30" "5k" "treble" "20" "12k"))

  (defn mix! [t1 t2 out]
    (shell/sh "sox" "-m"
              (str t1 ".wav") (str t2 ".wav")
              (str out ".wav")))

  (defn play! [file]
    (shell/sh "play" (str file ".wav")))

  (defn sample-exists? [wave pitch val tempo]
    (some #{(str  "samples/" wave "-" pitch "-" val "-" tempo ".wav")}
          (str/split-lines (:out (shell/sh "ls")))))

  (defn build-track!
    ([file wave tempo notes]
     (init-wav!)
     (println (str "Building track - \"" file "\" - Please wait..."))
     (doseq [[pitch val] notes]
       (when-not (sample-exists? wave pitch val tempo)
         (create-note! wave pitch val tempo)
       (append-note! wave pitch val tempo)))
    (shell/sh "mv" "samples/temp0.wav" (str file ".wav")))
  ([file wave tempo decay notes]
   (init-wav!)
   (println (str "Building track - \"" file "\" - Please wait..."))
   (doseq [[pitch val] notes]
     (when-not (sample-exists? wave pitch val tempo)
       (create-note! wave pitch val tempo decay))
     (append-note! wave pitch val tempo))
   (shell/sh "mv" "samples/temp0.wav" (str file ".wav"))))

;;;;;;;;;; Triangle bass/drums

(def tri-1
  {:deg-1 [[50 64] [46 64] [42 64] [40 64] [36 8] [0 32]]
   :deg-4 [[50 64] [46 64] [42 64] [40 64] [41 8] [0 32]]
   :deg-5 [[58 64] [55 64] [52 64] [42 64] [43 8] [0 32]]
   :deg-8 [[60 64] [48 8] [0 32]]
   :deg-11 [[65 64] [53 8] [0 32]]})

(def tri-2
  {:deg-8 [[60 64] [48 24] [58 64] [55 64] [52 64] [42 64] [48 64] [0 32]]})

(def intro-1
  (concat (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-1)))

(def intro-2
  (concat (:deg-1 tri-1) (:deg-8 tri-1) (:deg-5 tri-1) (:deg-8 tri-2)))

(def verse-2
  (concat (:deg-4 tri-1) (:deg-11 tri-1) (:deg-8 tri-1) (:deg-11 tri-1)))

(build-track! "intro-1" "triangle" 160 intro-1)
(build-track! "intro-2" "triangle" 160 intro-2)
(build-track! "verse-2" "triangle" 160 verse-2)
(concat-wav! "intro-1" "intro-2" "intro-A")
(concat-wav! "intro-A" "intro-A" "intro")
(concat-wav! "intro-1" "intro-1" "verse-1")
(concat-wav! "verse-1" "verse-1" "verse-2")
(concat-wav! "intro" "verse-2" "intro-verse-1")
(play! "intro-verse-1")

;;;;;;;;;;;;Sawtooth bass

(def saw-1
  {:deg-1 [[31 16] [31 16] [43 16] [43 16]]
   :deg-2 [[33 16] [33 16] [43 16] [45 16]]
   :deg-5 [[38 16] [38 16] [40 16] [40 16] [41 16] [41 16] [42 16] [42 16]]})

(def saw-2
  {:deg-5 [[38 16] [38 16] [48 16] [50 16] [31 16] [31 16] [43 8]]})

(build-track! "saw-1" "sawtooth" 95 0.25 (:deg-1 saw-1))
(build-track! "saw-2" "sawtooth" 95 0.25 (:deg-2 saw-1))
(build-track! "saw-5" "sawtooth" 95 0.25 (:deg-5 saw-1))
(build-track! "saw-5b" "sawtooth" 95 0.25 (:deg-5 saw-2))
(concat-wav! "saw-1" "saw-1" "saw-A")
(concat-wav! "saw-A" "saw-A" "saw-B")
(concat-wav! "saw-2" "saw-2" "saw-3")
(concat-wav! "saw-B" "saw-3" "saw-C")
(concat-wav! "saw-C" "saw-5" "saw-D")
(concat-wav! "saw-C" "saw-5b" "saw-E")
(concat-wav! "saw-D" "saw-E" "saw-F")
(play! "saw-F")
