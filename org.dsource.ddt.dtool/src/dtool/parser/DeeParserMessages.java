package dtool.parser;

public class DeeParserMessages {

	public static final String INVALID_TOKEN = 
		"Invalid tokens.";
	public static final String COMMENT_NOT_TERMINATED = 
		"Comment not terminated, expected a '*/'.";
	public static final String COMMENTNESTED_NOT_TERMINATED = 
		"Nested comment not terminated, expected a '+/'.";
	
	public static final String STRING_NOT_TERMINATED__REACHED_EOF = 
		"String not terminated, found EOF.";
	
	public static final String STRING_DELIM_NO_DELIMETER = 
		"Found EOF when expecting string delimeter.";
	public static final String STRING_DELIM_NOT_PROPERLY_TERMINATED = 
		"Expected '\"' after closing delimiter in delimited string.";
	public static final String STRING_DELIM_ID_NOT_PROPERLY_FORMED = 
		"Expected newline after identifier of delimited string.";
	
	public static final String CHAR_LITERAL_NOT_TERMINATED__REACHED_EOF = 
		"Character literal not terminated, found EOF.";
	public static final String CHAR_LITERAL_NOT_TERMINATED__REACHED_EOL = 
		"Character literal not terminated, found new line.";
	public static final String CHAR_LITERAL_EMPTY = 
		"Character literal is empty, no character specified.";
	
	public static final String INT_LITERAL_BINARY__INVALID_DIGITS = 
		"Invalid digits in binary literal";
	public static final String INT_LITERAL_OCTAL__INVALID_DIGITS = 
		"Invalid digits in octal literal";
	public static final String INT_LITERAL__HAS_NO_DIGITS = 
		"Literal has no digits, only underscores";
	
	public static final String FLOAT_LITERAL__EXP_HAS_NO_DIGITS = 
		"Exponent of float literal has no digits.";
	public static final String FLOAT_LITERAL__HEX_HAS_NO_EXP = 
		"Hexadecimal float literal has no exponent.";
	
	public static final String SPECIAL_TOKEN_LINE_BAD_FORMAT = 
		"Invalid format for #line token, must be #line integer [\"filespec\"]\n.";
	
	public static final String SPECIAL_TOKEN_INVALID = 
		"Invalid format for # pragma syntax.";
	
}