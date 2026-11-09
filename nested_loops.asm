; C line 1: /* Three outer iterations, two inner iterations each: total = 6.
; C line 2:    Both counters and the total stay within the DMN arithmetic range. */
; C line 3: int main(void) {
; C line 4:     unsigned char outer = 3;
LDI R1,#03
LDI R3,#00
STR R3,R1
; C line 5:     unsigned char inner = 0;
LDI R1,#00
LDI R3,#01
STR R3,R1
; C line 6:     unsigned char total = 0;
LDI R1,#00
LDI R3,#02
STR R3,R1
; C line 7: 
; C line 8:     while (outer != 0) {
C0:
LDI R3,#00
LDR R1,R3
LDI R3,#03
STR R3,R1
LDI R1,#00
OR R2,R1,R0
LDI R3,#03
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
; C line 9:         inner = 2;
LDI R1,#02
LDI R3,#01
STR R3,R1
; C line 10:         while (inner != 0) {
C4:
LDI R3,#01
LDR R1,R3
LDI R3,#03
STR R3,R1
LDI R1,#00
OR R2,R1,R0
LDI R3,#03
LDR R1,R3
ZTS R1,R2
JCN C7
OR R0,R0,R0
ZTS R0,R0
JCN C5
OR R0,R0,R0
C7:
ZTS R0,R0
JCN C6
OR R0,R0,R0
C5:
; C line 11:             total = total + 1;
LDI R3,#02
LDR R1,R3
LDI R3,#03
STR R3,R1
LDI R1,#01
OR R2,R1,R0
LDI R3,#03
LDR R1,R3
ADD R1,R1,R2
LDI R3,#02
STR R3,R1
; C line 12:             inner = inner - 1;
LDI R3,#01
LDR R1,R3
LDI R3,#03
STR R3,R1
LDI R1,#01
OR R2,R1,R0
LDI R3,#03
LDR R1,R3
SUB R1,R1,R2
LDI R3,#01
STR R3,R1
; C line 13:         }
ZTS R0,R0
JCN C4
OR R0,R0,R0
C6:
; C line 14:         outer = outer - 1;
LDI R3,#00
LDR R1,R3
LDI R3,#03
STR R3,R1
LDI R1,#01
OR R2,R1,R0
LDI R3,#03
LDR R1,R3
SUB R1,R1,R2
LDI R3,#00
STR R3,R1
; C line 15:     }
ZTS R0,R0
JCN C0
OR R0,R0,R0
C2:
; C line 16: 
; C line 17:     return total;
LDI R3,#02
LDR R1,R3
; C line 18: }
; C line 19: 
HLT
