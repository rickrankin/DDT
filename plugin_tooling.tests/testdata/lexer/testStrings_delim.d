▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ →◙  DELIM STRING - basic delim
q"/asdf" asdfd/"
q".asdf" asdfd."
q".."
q""asdf# asdfd""
q"(( )"  (())  asdf"  [<}  (xx"xx))"
q"[[ ]"  [[]]  asdf"  <{)  [xx"xx]]"
q"<< >"  <<>>  asdf"  {(]  <xx"xx>>"
q"{{ }"  {{}}  asdf"  ([>  {xx"xx}}"
◙LEXERTEST:
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  DELIM STRING - basic delim
q"/asdf/ asdfd"
q".. asdfd"
q""asdf" asdfd"
q"( asdf (asdf)) asdf" q"( asdf (asdf)) asdf)"
q"[ asdf [asdf]] asdf" q"[ asdf [asdf]] asdf]"
q"< asdf <asdf>> asdf" q"< asdf <asdf>> asdf>"
q"{ asdf {asdf}} asdf" q"{ asdf {asdf}} asdf}"
#LEXERTEST:
STRING_DELIM!SDx, EOL,
STRING_DELIM!SDx, EOL,
STRING_DELIM!SDx, EOL,
STRING_DELIM!SDx, WS, STRING_DELIM!SDx,EOL,
STRING_DELIM!SDx, WS, STRING_DELIM!SDx,EOL,
STRING_DELIM!SDx, WS, STRING_DELIM!SDx,EOL,
STRING_DELIM!SDx, WS, STRING_DELIM!SDx,EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
q"#LEXERTEST:
STRING_DELIM!SDxD
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
q"/#LEXERTEST:
STRING_DELIM!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
q"(#LEXERTEST:
STRING_DELIM!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
q"/asdf"
#LEXERTEST:
STRING_DELIM!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
q"(asdf("
#LEXERTEST:
STRING_DELIM!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
q"( asdf (asdf)" xxx#LEXERTEST:
STRING_DELIM!Sx

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  DELIM STRING - identifier delim
q"EOS
This is a multi-line " EOS
EOS
heredoc string
EOS"EOS
q"abc
"
"abc
abc
abc"
q"a
a"
#LEXERTEST:
STRING_DELIM,ID,EOL,
STRING_DELIM,EOL,
STRING_DELIM,EOL,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q"xx asdf 
xx"
foobar#LEXERTEST:
STRING_DELIM!SDxID,EOL,ID
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q"xxx#LEXERTEST:
STRING_DELIM!SDxID
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q"xxx"asdf#LEXERTEST:
STRING_DELIM!SDxID
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q"xxx blah#LEXERTEST:
STRING_DELIM!SDxID
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (invalid char(space) after id)
q"xxx 
xxx"foobar#LEXERTEST:
STRING_DELIM!SDxID,ID
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (invalid char after id, test recovery)
q"xxx!
asd "
xxx 
xxx" foobar#LEXERTEST:
STRING_DELIM!SDxID,WS,ID
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q"xxx
xxx "foobar#LEXERTEST:
STRING_DELIM!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q"xxx
xxx#LEXERTEST:
STRING_DELIM!Sx


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ →◙ TOKEN STRING
q{}
q{ asdf __TIME__  {nest braces} q"[{]" { q{nestedToken } String} }
q{asdf 
/* } */ {
// }  
}
"}" blah  }xxx
q{asdf {
` aaa` }
}
q{#!/usrs }
}
◙LEXERTEST:
STRING_TOKENS,EOL,
STRING_TOKENS,EOL,
STRING_TOKENS,ID,EOL,
STRING_TOKENS,EOL,
STRING_TOKENS,EOL,


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q{#LEXERTEST:
STRING_TOKENS!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q{ aasdf
#LEXERTEST:
STRING_TOKENS!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q{ aasdf
/*asdf
#LEXERTEST:
STRING_TOKENS!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q{ asas sdas }
#LEXERTEST:
STRING_TOKENS!Sx
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q{ sdaa }
#LEXERTEST:
STRING_TOKENS!Sx

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
q{ __EOF__ }
#LEXERTEST:
STRING_TOKENS!Sx,
