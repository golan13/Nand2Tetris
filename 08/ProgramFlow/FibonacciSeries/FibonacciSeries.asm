//push argument 1
@1
D=A
@ARG
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
//pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D
//push constant 0
@0
D=A
@SP
M=M+1
A=M-1
M=D
//pop that 0
@0
D=A
@THAT
D=M+D
@13
M=D
@SP
AM=M-1
D=M
@13
A=M
M=D
//push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D
//pop that 1
@1
D=A
@THAT
D=M+D
@13
M=D
@SP
AM=M-1
D=M
@13
A=M
M=D
//push argument 0
@0
D=A
@ARG
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
//push constant 2
@2
D=A
@SP
M=M+1
A=M-1
M=D
//sub
@SP
M=M-1
A=M
D=-M
A=A-1
D=M+D
M=D
//pop argument 0
@0
D=A
@ARG
D=M+D
@13
M=D
@SP
AM=M-1
D=M
@13
A=M
M=D
//label MAIN_LOOP_START
(null$MAIN_LOOP_START)
//push argument 0
@0
D=A
@ARG
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
//if-goto COMPUTE_ELEMENT
@SP
AM=M-1
D=M
@null$COMPUTE_ELEMENT
D;JNE
//goto END_PROGRAM
@null$END_PROGRAM
0;JMP
//label COMPUTE_ELEMENT
(null$COMPUTE_ELEMENT)
//push that 0
@0
D=A
@THAT
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
//push that 1
@1
D=A
@THAT
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
//add
@SP
M=M-1
A=M
D=M
A=A-1
D=M+D
M=D
//pop that 2
@2
D=A
@THAT
D=M+D
@13
M=D
@SP
AM=M-1
D=M
@13
A=M
M=D
//push pointer 1
@THAT
D=M
@SP
M=M+1
A=M-1
M=D
//push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D
//add
@SP
M=M-1
A=M
D=M
A=A-1
D=M+D
M=D
//pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D
//push argument 0
@0
D=A
@ARG
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
//push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D
//sub
@SP
M=M-1
A=M
D=-M
A=A-1
D=M+D
M=D
//pop argument 0
@0
D=A
@ARG
D=M+D
@13
M=D
@SP
AM=M-1
D=M
@13
A=M
M=D
//goto MAIN_LOOP_START
@null$MAIN_LOOP_START
0;JMP
//label END_PROGRAM
(null$END_PROGRAM)
