; C line 1: /* Sum the first eight positive Fibonacci values by parity.
; C line 2:    Terms: 1, 1, 2, 3, 5, 8, 13, 21.
; C line 3:    evenSum = 10, oddSum = 44, checksum = 54.
; C line 4:    Every addition and subtraction stays within the DMN safe range. */
; C line 5: int main(void) {
; C line 6:     unsigned char count = 8;
LDI R1,#08
LDI R3,#00
STR R3,R1
; C line 7:     unsigned char previous = 0;
LDI R1,#00
LDI R3,#01
STR R3,R1
; C line 8:     unsigned char current = 1;
LDI R1,#01
LDI R3,#02
STR R3,R1
; C line 9:     unsigned char next = 0;
LDI R1,#00
LDI R3,#03
STR R3,R1
; C line 10:     unsigned char evenSum = 0;
LDI R1,#00
LDI R3,#04
STR R3,R1
; C line 11:     unsigned char oddSum = 0;
LDI R1,#00
LDI R3,#05
STR R3,R1
; C line 12:     unsigned char checksum = 0;
LDI R1,#00
LDI R3,#06
STR R3,R1
; C line 13: 
; C line 14:     while (count != 0) {
C0:
LDI R3,#00
LDR R1,R3
LDI R3,#07
STR R3,R1
LDI R1,#00
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
ZTS R1,R2
JCN C3
OR R0,R0,R0
ZTS R0,R0
JCN C1
OR R0,R0,R0
C3:
ZTS R0,R0
JCN C2
OR R0,R0,R0
C1:
; C line 15:         if ((current & 1) == 0) {
LDI R3,#02
LDR R1,R3
LDI R3,#08
STR R3,R1
LDI R1,#01
OR R2,R1,R0
LDI R3,#08
LDR R1,R3
AND R1,R1,R2
LDI R3,#07
STR R3,R1
LDI R1,#00
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
ZTS R1,R2
JCN C4
OR R0,R0,R0
; C line 17:         } else {
; C line 18:             oddSum = oddSum + current;
LDI R3,#05
LDR R1,R3
LDI R3,#07
STR R3,R1
LDI R3,#02
LDR R1,R3
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
ADD R1,R1,R2
LDI R3,#05
STR R3,R1
; C line 19:         }
ZTS R0,R0
JCN C5
OR R0,R0,R0
C4:
; C line 16:             evenSum = evenSum + current;
LDI R3,#04
LDR R1,R3
LDI R3,#07
STR R3,R1
LDI R3,#02
LDR R1,R3
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
ADD R1,R1,R2
LDI R3,#04
STR R3,R1
; C line 17:         } else {
C5:
; C line 20: 
; C line 21:         next = previous + current;
LDI R3,#01
LDR R1,R3
LDI R3,#07
STR R3,R1
LDI R3,#02
LDR R1,R3
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
ADD R1,R1,R2
LDI R3,#03
STR R3,R1
; C line 22:         previous = current;
LDI R3,#02
LDR R1,R3
LDI R3,#01
STR R3,R1
; C line 23:         current = next;
LDI R3,#03
LDR R1,R3
LDI R3,#02
STR R3,R1
; C line 24:         count = count - 1;
LDI R3,#00
LDR R1,R3
LDI R3,#07
STR R3,R1
LDI R1,#01
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
SUB R1,R1,R2
LDI R3,#00
STR R3,R1
; C line 25:     }
ZTS R0,R0
JCN C0
OR R0,R0,R0
C2:
; C line 26: 
; C line 27:     checksum = evenSum + oddSum;
LDI R3,#04
LDR R1,R3
LDI R3,#07
STR R3,R1
LDI R3,#05
LDR R1,R3
OR R2,R1,R0
LDI R3,#07
LDR R1,R3
ADD R1,R1,R2
LDI R3,#06
STR R3,R1
; C line 28:     return checksum;
LDI R3,#06
LDR R1,R3
; C line 29: }
; C line 30: 
HLT
