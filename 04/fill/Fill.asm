// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed.
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

(LOOP)
  @i            //load i
  M=0           //i=0
  @8192
  D=A
  @R0
  M=D           //RAM[0] = 8192 (size of screen)
  @KBD          //load Keyboard
  D=M           //D=keyboard
  @SCREENON
  D;JNE         //If keyboard is pressed, jump to SCREENON
  @SCREENOFF
  D;JEQ         //If keyboard not pressed, jump to LOOP
(SCREENON)      //Function to turn screen on
  @i            //variable to assist pointer
  D=M           //D=i
  @SCREEN
  A=A+D         //pointer to screen pixel
  M=-1          //set screen row to black
  @i
  M=M+1         //increase pointer
  @R0           //load R0
  M=M-1         //subtract from counter
  D=M
  @SCREENON
  D;JNE         //If screen not fully black, jump to SCREENON
  @LOOP
  0;JMP         //If screen fully black, jump to LOOP
(SCREENOFF)     //Function to turn screen off
  @i            //variable to assist pointer
  D=M           //D=i
  @SCREEN
  A=A+D         //pointer to screen pixel
  M=0           //set screen row to white
  @i
  M=M+1         //increase pointer
  @R0           //load R0
  M=M-1         //subtract from counter
  D=M
  @SCREENOFF
  D;JNE         //If screen not fully white, jump to SCREENOFF
  @LOOP
  0;JMP         //If screen fully white, jump to LOOP
