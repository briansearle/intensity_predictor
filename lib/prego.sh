#!/bin/sh

java -cp "./*" edu.washington.maccoss.intensity_predictor.Classify -p -nn new_jarrett_intensities.nn -r "$2" "$1"
