▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ WYSIWYG ALT string 
safd` asdf \x\f      /*sdf asd`asdf``a
safd` asdf \a\f\ NUL /*sdf asd`c
safd` asdf \\\f      /*sdf asd`w
safd` asdf \\\f      /*sdf asd`d
safd` asdf
 \\\f /*sdf asd`dfoobar
#LEXERTEST:
ID,STRING_WYSIWYG,ID,STRING_WYSIWYG,ID,EOL,
ID,STRING_WYSIWYG,EOL,
ID,STRING_WYSIWYG,EOL,
ID,STRING_WYSIWYG,EOL,
ID,STRING_WYSIWYG,ID,EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
foo` asdf \x\f /*
asdf
#LEXERTEST:
ID,STRING_WYSIWYG!Sx

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ WYSIWYG string 
r" asfdd \f/*sdf asd"asdf r""a
r" asdf \x\f\ NUL /*sdf asd"c
r" asdf \a\f /*sdf asd"w
r" asdf \\\f /*sdf asd"d
r" asdf
 \\\f /*sdf asd"dfoobar
xxr"asdf"rrr"asdf"
rambo rrrrr
#LEXERTEST:
STRING_WYSIWYG,ID,WS,STRING_WYSIWYG,ID,EOL,
STRING_WYSIWYG,EOL,
STRING_WYSIWYG,EOL,
STRING_WYSIWYG,EOL,
STRING_WYSIWYG,ID,EOL,
ID,STRING_DQ,ID,STRING_DQ,EOL,
ID,WS,ID,EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
r" asdf \x\f /*

#LEXERTEST:
STRING_WYSIWYG!Sx

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DOUBLE QUOTED string 
saf" asfdd \f/*sdf asd"asdf""a
" asfdd \f/*sdf asd"c
" asfdd \f/*sdf asd"w
" asfdd \f/*sdf asd"d
R" asfdd \f/*sdf asd"D
#LEXERTEST:
ID,STRING_DQ,ID,STRING_DQ,ID,EOL,
STRING_DQ,EOL,
STRING_DQ,EOL,
STRING_DQ,EOL,
ID,STRING_DQ,ID,EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DQ_STRING - error
foo" asdf /*

#LEXERTEST:
ID,STRING_DQ!Sx

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DQ_STRING - escape sequences and weird characters
" \"asfd/*sdf asd"  "\"" "\\"foo
"  testing newline inside string

"c
"   \'  \"  \?  \\  \a  \b  \f  \n  \r  \t  \v  \ NUL
  \x0A\xF2
  \123\12\1xx
  \u00DA\uF1DAxx
  \U00DAF1DA\U123456768\UABCDEF012xxx
  \&quot;\&amp;\&lt;  // NamedCharacterEntity
    /*aaasasasassa "w
foobar
#LEXERTEST:
STRING_DQ,WS,STRING_DQ,WS,STRING_DQ,ID,EOL,
STRING_DQ,EOL,
STRING_DQ,EOL,
ID,EOL

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DQ_STRING - escape sequences (invalid)
foo"  \c" "  \z" "  \xZZ"
"  \u" "  \uFFA" "  \uFFAZ" "  \uZZ12"
"  \U" "  \UFFA" "  \U1234FFAZ" "  \u1234ZZ12"
"  \&quotxxx;\&xxxamp;\&xxlt;"
#LEXERTEST:
ID,STRING_DQ,WS,STRING_DQ,WS,STRING_DQ,EOL,
STRING_DQ,WS,STRING_DQ,WS,STRING_DQ,WS,STRING_DQ,EOL,
STRING_DQ,WS,STRING_DQ,WS,STRING_DQ,WS,STRING_DQ,EOL,
STRING_DQ,EOL,
Ⓗ▂▂▂
// note: on the above we still expect string tokens and not errors  
// because escape sequences will be analized in post-lexer pass.


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DQ_STRING - escape sequences + EOF
"aa \#LEXERTEST:
STRING_DQ!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
"aa \x#LEXERTEST:
STRING_DQ!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
"aa \u#LEXERTEST:
STRING_DQ!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
"aa \&#LEXERTEST:
STRING_DQ!Sx

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ HEX string 
x"asfdd \f/*sdf asd"asdf x""a
x" asfdd \f/*sdf asd"c
x" asfdd \f/*sdf asd"w
x" asfdd \f/*sdf asd"d
X" asfdd \f/*sdf asd"dfoobar
#LEXERTEST:
STRING_HEX,ID,WS,STRING_HEX,ID,EOL,
STRING_HEX,EOL,
STRING_HEX,EOL,
STRING_HEX,EOL,
ID,STRING_DQ,ID,EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
x"aa #LEXERTEST:
STRING_HEX!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
x"aa \#LEXERTEST:
STRING_HEX!Sx