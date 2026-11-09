; C line 1: int main(void) {
; C line 2:     unsigned char count = 3;
LDI R1,#03
LDI R3,#00
STR R3,R1
; C line 3:     unsigned char total = 0;
LDI R1,#00
LDI R3,#01
STR R3,R1
; C line 4:     while (count != 0) {
C0:
LDI R3,#00
LDR R1,R3
LDI R3,#02
STR R3,R1
LDI R1,#00
OR R2,R1,R0
LDI R3,#02
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
; C line 5:         total = total + count;
LDI R3,#01
LDR R1,R3
LDI R3,#02
STR R3,R1
LDI R3,#00
LDR R1,R3
OR R2,R1,R0
LDI R3,#02
LDR R1,R3
ADD R1,R1,R2
LDI R3,#01
STR R3,R1
; C line 6:         count = count - 1;
LDI R3,#00
LDR R1,R3
LDI R3,#02
STR R3,R1
LDI R1,#01
OR R2,R1,R0
LDI R3,#02
LDR R1,R3
SUB R1,R1,R2
LDI R3,#00
STR R3,R1
; C line 7:     }
ZTS R0,R0
JCN C0
OR R0,R0,R0
C2:
; C line 8:     return total;
LDI R3,#01
LDR R1,R3
; C line 9: }
; C line 10: 
HLT
