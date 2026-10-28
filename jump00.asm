    LDI R1,#05
    LDI R2,#05
    ZTS R1,R2
    JCN J1
    LDI R3,#00
    HLT
    HLT
    HLT
J1: add r1,r0,r0
    or r5,r0,r0
    hlt