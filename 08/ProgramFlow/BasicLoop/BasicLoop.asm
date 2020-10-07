//push constant 0
@0
D=A
@SP
M=M+1
A=M-1
M=D
//pop local 0
@0
D=A
@LCL
D=M+D
@13
M=D
@SP
AM=M-1
D=M
@13
A=M
M=D
//label LOOP_START
(null$LOOP_START)
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
//push local 0
@0
D=A
@LCL
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
//pop local 0
@0
D=A
@LCL
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
//if-goto LOOP_START
@SP
AM=M-1
D=M
@null$LOOP_START
D;JNE
//push local 0
@0
D=A
@LCL
A=M+D
D=M
@SP
M=M+1
A=M-1
M=D
