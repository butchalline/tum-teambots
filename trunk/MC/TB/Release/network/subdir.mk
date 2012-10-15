################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../network/Usb.cpp 

OBJS += \
./network/Usb.o 

CPP_DEPS += \
./network/Usb.d 


# Each subdirectory must supply rules for building sources it contributes
network/%.o: ../network/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: AVR C++ Compiler'
	avr-g++ -I"C:\dev\tools\arduino-1.0.1\hardware\arduino\cores\arduino" -I"C:\dev\tools\arduino-1.0.1\hardware\arduino\variants\mega" -I"C:\dev\TeamBots\MC\TB" -I"C:\dev\TeamBots\MC\libraries\AndroidAccessory" -I"C:\dev\TeamBots\MC\libraries\USBHost" -D__IN_ECLIPSE__=1 -DUSB_VID= -DUSB_PID= -DARDUINO=101 -Wall -Os -ffunction-sections -fdata-sections -fno-exceptions -g -mmcu=atmega2560 -DF_CPU=16000000UL -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"  -c -o "$@" -x c++ "$<"
	@echo 'Finished building: $<'
	@echo ' '


