// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

  @R2            //load RAM[2]
  M=0            //Set RAM[2]=0
(LOOP)
  @R1            //load RAM[1]
  D=M            //D=RAM[1]
  @END
  D;JEQ          //If D==0 jump to END
  @R0            //load RAM[0]
  D=M            //D=RAM[0]
  @R2            //load RAM[2]
  M=D+M          //RAM[2] = RAM[0] + RAM[2]
  @R1            //load RAM[1]
  M=M-1          //RAM[1]--
  @LOOP
  0;JMP          //Jump to LOOP
(END)
  @END
  0;JMP
