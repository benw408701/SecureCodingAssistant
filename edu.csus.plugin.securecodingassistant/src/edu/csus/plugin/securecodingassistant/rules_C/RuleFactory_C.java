package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;

/**
 * Creates a set of rules that implement the <code>IRule_C</code> interface
 * @author Victor Melnik
 * @see IRule_C
 *  
 * Design borrowed from @author Benjamin White
 * @see RuleFactory
 */
public final class RuleFactory_C {
	/**
	 * Cannot instantiate
	 */
	private RuleFactory_C() {
		
	}
	
	/**
	 * Creates a collection of all possible rules to test
	 * @return A collection of all possible rules to test
	 */
	public static ArrayList<IRule_C> getAllCERTRules() {
		ArrayList<IRule_C> rules = new ArrayList<IRule_C>();
		
		//CERT Secure Coding Rules for C
		
		//Rule 02. Declarations and Initialization (DCL)
		rules.add(new DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember());
		rules.add(new DCL41C_DoNotDeclareVariablesInsideSwitchStatement()); 
		rules.add(new DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification());
		
		//Rule 03. Expressions (EXP)
		rules.add(new EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece());
		
		//Rule 04. Integers (INT)
		rules.add(new INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError());
		
		//Rule 05. Floating Point (FLP)
		rules.add(new FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter()); 
		
		//Rule 06. Arrays (ARR)
		rules.add(new ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray());
		
		//Rule 07. Characters and Strings (STR)
		rules.add(new STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions());
		rules.add(new STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes());
		rules.add(new STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar());
		
		//Rule 08. Memory Management (MEM)
		rules.add(new MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded());
		
		//Rule 09. Input Output (FIO)
		rules.add(new FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles()); 
		rules.add(new FIO47C_UseValidFormatStrings());
		
		//Rule 10. Environment (ENV)
		rules.add(new ENV33C_DoNotCallSystem());
		
		//Rule 11. Signals (SIG)
		rules.add(new SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers());
		rules.add(new SIG31C_DoNotAccessSharedObjectsInSignalHandlers());
		
		//Rule 12. Error Handling (ERR)
		rules.add(new ERR34C_DetectErrorsWhenConvertingStringToNumber());
		
		//Rule 14. Concurrency (CON)
		rules.add(new CON40C_DoNotReferToAtomicVariableTwiceinExpression());
		
		//Rule 48. Miscellaneous (MSC)
		rules.add(new MSC30C_DoNotUseRandFunctionForGeneratingPseudorandomNumbers());
		
		//Rule 50. POSIX (POS)
		rules.add(new POS3C_DoNotUseVfork()); 
				
		return rules;
	}
	
}
