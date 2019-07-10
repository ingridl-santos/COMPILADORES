
sumA:	enter 1 1
	load 0
	getstatic 0
	add
	exit	
	return

_main_:	enter 0 0
	const 4
	putstatic 0
	prints "3+a: "
	const 3
	call sumA
	printi
	prints "\n"
	exit 
	return


