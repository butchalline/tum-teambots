################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
C:/dev/TeamBots/MC/libraries/USBHost/Max3421e.cpp \
C:/dev/TeamBots/MC/libraries/USBHost/Usb.cpp 

OBJS += \
./USBHost/Max3421e.o \
./USBHost/Usb.o 

CPP_DEPS += \
./USBHost/Max3421e.d \
./USBHost/Usb.d 


# Each subdirectory must supply rules for building sources it contributes
USBHost/Max3421e.o: C:/dev/TeamBots/MC/libraries/USBHost/Max3421e.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"C:\dev\tools\arduino-1.0.1\hardware\arduino\cores\arduino" -I"C:\dev\tools\arduino-1.0.1\hardware\arduino\variants\mega" -I"C:\dev\TeamBots\MC\TB" -I"C:\dev\TeamBots\MC\libraries\AndroidAccessory" -I"C:\dev\TeamBots\MC\libraries\USBHost" -I"C:\dev\TeamBots\MC\libraries\Dynamixel_Serial" -D__IN_ECLIPSE__=1 -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -ffunction-sections -fdata-sections -fno-exceptions -g -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" -x c++ "$<"
	@echo 'Finished building: $<'
	@echo ' '

USBHost/Usb.o: C:/dev/TeamBots/MC/libraries/USBHost/Usb.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"C:\dev\tools\arduino-1.0.1\hardware\arduino\cores\arduino" -I"C:\dev\tools\arduino-1.0.1\hardware\arduino\variants\mega" -I"C:\dev\TeamBots\MC\TB" -I"C:\dev\TeamBots\MC\libraries\AndroidAccessory" -I"C:\dev\TeamBots\MC\libraries\USBHost" -I"C:\dev\TeamBots\MC\libraries\Dynamixel_Serial" -D__IN_ECLIPSE__=1 -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -ffunction-sections -fdata-sections -fno-exceptions -g -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" -x c++ "$<"
	@echo 'Finished building: $<'
	@echo ' '


