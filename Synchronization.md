# Timer Synchronization #

The Timer synchronization between the SmartPhone and the ArduinoMC is handled as followed:

  1. The first message received by the MC is the Set of the SmartPhone Timer unit. e.g. the first Massage has a TimeStamp of 100ms
  1. It is assumed that both Timers run in the same speed and are correct. Our first message includes the delay from MC to SmartPhone. All further messages are compared to the SmartPhone value. If the value is smaller then the SmartPhone time we have a decrease of our transfer delay. If it is bigger, we have an increase.

Example
  1. First Message TimeStamp: 100 ms = SP\_TimerStart (+ Unknown Delay X)
  1. Second Message TimeStamp: 150 ms; SP\_Timer = 145ms : The Unknown Delay has a Delta of -5ms

The Delay delta is updated every Message.

It is assumed that the minimal delay of the usb connection is neglectable (it actually is never measured).