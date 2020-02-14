# chip-sox

This is a library to facilitate live composition and performance of Chiptune music in the style of the [NES](http://famitracker.com/wiki/index.php?title=Sound_hardware#Nintendo_MMC5) and [C64](https://en.wikipedia.org/wiki/MOS_Technology_6581).

It is sample-based for performance on systems with limited resources, but generates custom waveforms with [SoX](http://sox.sourceforge.net/sox.html). Through the magic of [babashka](https://github.com/borkdude/babashka), tracks are built interactively by composing functional building blocks in your editor over a socket REPL. 


## Installation

1. Install SoX:

```bash
$ sudo apt install sox
```

2. Install [babashka](https://github.com/borkdude/babashka#installation).

## Usage

1. Start up a socket REPL from the project directory:

```bash
$ bb --socket-repl 1666
Babashka socket REPL started at localhost:1666
```

2. Open up `bb-sox.clj` in your Clojure editor and connect to the running socket REPL.
