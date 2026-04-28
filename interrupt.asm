		ldi re,intr
		ldi r1,#01
		ldi r2,#0a
loop:	sub r2,r2,r1
		or  r3,r2,r3
		sub r4,r2,r3
		nts r0,r2
		jcn loop
		hlt
intr:   ldi r5,#10
		ldi r6,#01
		and r6,r5,r6
		rti
		hlt
