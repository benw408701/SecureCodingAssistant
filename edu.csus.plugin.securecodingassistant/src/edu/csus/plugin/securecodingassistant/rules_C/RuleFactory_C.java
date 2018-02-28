package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;

/**
 * Creates a set of rules that implement the <code>IRule_C</code> interface
 * @author Victor Melnik
 * @see IRule_C
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
		
		//TEST!!!!!!
		
		//
		rules.add(new TEST_RULE());
		
		//Rule 01. Preprocessor (PRE)
		//rules.add(new PRE30C_DoNotCreateUniversalCharacterNameThroughConcatenation());
		
		//Rule 02. Declarations and Initialization (DCL)
		rules.add(new DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember());//rule implemented
		rules.add(new DCL41C_DoNotDeclareVariablesInsideSwitchStatement()); //rule implemented
		rules.add(new DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification()); //rule implemented
		
		//Rule 03. Expressions (EXP)
		rules.add(new EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece()); //rule implemented
	//	rules.add(new EXP40C_DoNotModifyConstObjects());
	//	rules.add(new EXP34C_DoNotDereferenceNullPointers()); //rule not-fully implemented
		
		//Rule 04. Integers (INT)
		rules.add(new INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError()); //rule implemented
		
		//Rule 05. Floating Point (FLP)
		rules.add(new FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter()); //rule implemented
		
		//Rule 06. Arrays (ARR)
	//	rules.add(new ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray()); //rule implemented
		
		//Rule 07. Characters and Strings (STR)
		rules.add(new STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions());// rule implemented
	//	rules.add(new STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes());//rule implemented
	//	rules.add(new STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar()); //rule implemented
		
		//Rule 08. Memory Management (MEM)
	//	rules.add(new MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded()); //rule implemented
		
		//Rule 09. Input Output (FIO)
	//	rules.add(new FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles()); //rule implemented
		
		
		//Rule 10. Environment (ENV)
	//	rules.add(new ENV33C_DoNotCallSystem()); //rule implemented
		
		//Rule 11. Signals (SIG)
	//	rules.add(new SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers());// rule implemented
		//rules.add(new SIG31C_DoNotAccessSharedObjectsInSignalHandlers()); //rule implemented (needs tuning)
		
		//Rule 12. Error Handling (ERR)
	//	rules.add(new ERR34C_DetectErrorsWhenConvertingStringToNumber()); //rule implemented
		
		//Rule 14. Concurrency (CON)
	//	rules.add(new CON40C_DoNotReferToAtomicVariableTwiceinExpression()); //rule implemented
		
		//Rule 48. Miscellaneous (MSC)
	//	rules.add(new MSC30C_DoNotUseRandFunctionForGeneratingPseudorandomNumbers());//rule implemented
		
		//Rule 50. POSIX (POS)
	//	rules.add(new POS3C_DoNotUseVfork()); //rule implemented
				
		return rules;
	}
	
}
