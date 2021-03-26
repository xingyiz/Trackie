# Trackie

USE THE SECOND TRACKIE 

## Esspresso Testing Setup

Upon launching your emulator, make sure you disable system animations on the virtual or physical devices used for testing. On your device, under Settings > Developer options, disable the following 3 settings:

- Window animation scale
- Transition animation scale
- Animator duration scale

Alternatively, ensure adb has been added to your path and run the following in your terminal.

```
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```
