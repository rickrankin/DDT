//#SPLIT_SOURCE_TEST _____________________ Char Literal
'a''"'
/+#LEXERTEST
CHAR_LITERAL,CHAR_LITERAL,EOL
+/
//#SPLIT_SOURCE_TEST _____________________ technically this seems to be according to grammar, but DMD rejects it
'''/+#LEXERTEST
CHAR_LITERAL!,CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST _____________________ 
''/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST _____________________
'a/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST _____________________
'/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST _____________________
'
CHAR_LITERAL!,CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST _____________________ >> oversized char literal, this is invalid but error is pseudo-semantic?
'aaa'foobar/+#LEXERTEST
CHAR_LITERAL,ID+/

//#SPLIT_SOURCE_TEST _____________________ Char Literal
'\'' '\\'
'\xFF'
'\u0123'
'\U0123ABCD'
'\123'
'\&quote'
/+#LEXERTEST
CHAR_LITERAL,WS,CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
+/

//#SPLIT_SOURCE_TEST _____________________ Char Literal, incomplete escapes
'\xF'
'\u012'
'\U0123ABC'
'\&'
/+#LEXERTEST
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
CHAR_LITERAL,EOL,
+/
//#SPLIT_SOURCE_TEST _______________  escape sequences + EOF
'\/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\
CHAR_LITERAL!, CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\x/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\u/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\U/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\u012/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\u0123ABC/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\1/+#LEXERTEST
CHAR_LITERAL!+/
//#SPLIT_SOURCE_TEST ________
'\&/+#LEXERTEST
CHAR_LITERAL!+/