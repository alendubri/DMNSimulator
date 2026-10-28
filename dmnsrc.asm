		ldi r1,#1
		ldi r2,#a
loop:	sub r2,r2,r1
		or  r3,r2,r3
		sub r4,r2,r3
		nts r0,r2
		jcn loop
		hlt
