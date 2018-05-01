################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray.c \
../src/CON40C_DoNotReferToAtomicVariableTwiceinExpression.c \
../src/DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification.c \
../src/DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember.c \
../src/DCL41C_DoNotDeclareVariablesInsideSwitchStatement.c \
../src/ENV33-C_Do_not_call_system.c \
../src/ERR34C_DetectErrorsWhenConvertingStringToNumber.c \
../src/EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece.c \
../src/FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles.c \
../src/FIO47C_UseValidFormatStrings.c \
../src/FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter.c \
../src/INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError.c \
../src/MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded.c \
../src/MSC30_C\ Do\ not\ use\ the\ rand\ function\ pseudorandom\ numbers.c \
../src/POS33_C_Do_not_call_vfork.c \
../src/SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers.c \
../src/SIG31C_DoNotAccessSharedObjectsInSignalHandlers.c \
../src/STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes.c \
../src/STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar.c \
../src/STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions.c 

OBJS += \
./src/ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray.o \
./src/CON40C_DoNotReferToAtomicVariableTwiceinExpression.o \
./src/DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification.o \
./src/DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember.o \
./src/DCL41C_DoNotDeclareVariablesInsideSwitchStatement.o \
./src/ENV33-C_Do_not_call_system.o \
./src/ERR34C_DetectErrorsWhenConvertingStringToNumber.o \
./src/EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece.o \
./src/FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles.o \
./src/FIO47C_UseValidFormatStrings.o \
./src/FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter.o \
./src/INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError.o \
./src/MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded.o \
./src/MSC30_C\ Do\ not\ use\ the\ rand\ function\ pseudorandom\ numbers.o \
./src/POS33_C_Do_not_call_vfork.o \
./src/SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers.o \
./src/SIG31C_DoNotAccessSharedObjectsInSignalHandlers.o \
./src/STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes.o \
./src/STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar.o \
./src/STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions.o 

C_DEPS += \
./src/ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray.d \
./src/CON40C_DoNotReferToAtomicVariableTwiceinExpression.d \
./src/DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification.d \
./src/DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember.d \
./src/DCL41C_DoNotDeclareVariablesInsideSwitchStatement.d \
./src/ENV33-C_Do_not_call_system.d \
./src/ERR34C_DetectErrorsWhenConvertingStringToNumber.d \
./src/EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece.d \
./src/FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles.d \
./src/FIO47C_UseValidFormatStrings.d \
./src/FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter.d \
./src/INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError.d \
./src/MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded.d \
./src/MSC30_C\ Do\ not\ use\ the\ rand\ function\ pseudorandom\ numbers.d \
./src/POS33_C_Do_not_call_vfork.d \
./src/SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers.d \
./src/SIG31C_DoNotAccessSharedObjectsInSignalHandlers.d \
./src/STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes.d \
./src/STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar.d \
./src/STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/MSC30_C\ Do\ not\ use\ the\ rand\ function\ pseudorandom\ numbers.o: ../src/MSC30_C\ Do\ not\ use\ the\ rand\ function\ pseudorandom\ numbers.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"src/MSC30_C Do not use the rand function pseudorandom numbers.d" -MT"src/MSC30_C\ Do\ not\ use\ the\ rand\ function\ pseudorandom\ numbers.d" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


