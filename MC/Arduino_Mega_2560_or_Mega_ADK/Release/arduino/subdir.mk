################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/CDC.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/HID.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/HardwareSerial.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/IPAddress.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/Print.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/Stream.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/Tone.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/USBCore.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/WMath.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/WString.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/main.cpp \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/new.cpp 

C_SRCS += \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/WInterrupts.c \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring.c \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_analog.c \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_digital.c \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_pulse.c \
D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_shift.c 

OBJS += \
./arduino/CDC.o \
./arduino/HID.o \
./arduino/HardwareSerial.o \
./arduino/IPAddress.o \
./arduino/Print.o \
./arduino/Stream.o \
./arduino/Tone.o \
./arduino/USBCore.o \
./arduino/WInterrupts.o \
./arduino/WMath.o \
./arduino/WString.o \
./arduino/main.o \
./arduino/new.o \
./arduino/wiring.o \
./arduino/wiring_analog.o \
./arduino/wiring_digital.o \
./arduino/wiring_pulse.o \
./arduino/wiring_shift.o 

C_DEPS += \
./arduino/WInterrupts.d \
./arduino/wiring.d \
./arduino/wiring_analog.d \
./arduino/wiring_digital.d \
./arduino/wiring_pulse.d \
./arduino/wiring_shift.d 

CPP_DEPS += \
./arduino/CDC.d \
./arduino/HID.d \
./arduino/HardwareSerial.d \
./arduino/IPAddress.d \
./arduino/Print.d \
./arduino/Stream.d \
./arduino/Tone.d \
./arduino/USBCore.d \
./arduino/WMath.d \
./arduino/WString.d \
./arduino/main.d \
./arduino/new.d 


# Each subdirectory must supply rules for building sources it contributes
arduino/CDC.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/CDC.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/HID.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/HID.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/HardwareSerial.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/HardwareSerial.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/IPAddress.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/IPAddress.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/Print.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/Print.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/Stream.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/Stream.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/Tone.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/Tone.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/USBCore.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/USBCore.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/WInterrupts.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/WInterrupts.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/WMath.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/WMath.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/WString.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/WString.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/main.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/main.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/new.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/new.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_PID= -DUSB_VID= -DARDUINO=101 -Wall -Os -fno-exceptions -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/wiring.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/wiring_analog.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_analog.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/wiring_digital.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_digital.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/wiring_pulse.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_pulse.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

arduino/wiring_shift.o: D:/01_Master_TUM/10_Teambots/arduino-1.0.1-windows/arduino-1.0.1/hardware/arduino/cores/arduino/wiring_shift.c
	@echo 'Building file: $<'
	@echo 'Invoking: AVR Compiler'
	avr-gcc -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\cores\arduino" -I"D:\01_Master_TUM\10_Teambots\arduino-1.0.1-windows\arduino-1.0.1\hardware\arduino\variants\mega" -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -g  -ffunction-sections  -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


