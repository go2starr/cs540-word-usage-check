all:	*.java
	javac *.java

tests:	cTests bTests


## CBR Tests
cTests:	cAmong cAccept cGood cTheir
cAmong:	all
	java CBR among between .234 among-between.train among-between.test 
cAccept: all
	java CBR accept except .234 accept-except.train accept-except.test 
cGood:	all
	java CBR good well .234 good-well.train good-well.test 
cTheir:	all
	java CBR their there .234 their-there.train their-there.test 

## Bayes Tests
bTests:	bAmong bAccept bGood bTheir
bAmong:	all
	java BayesNet among between .234 among-between.train among-between.test 
bAccept:all
	java BayesNet accept except .234 accept-except.train accept-except.test 
bGood: all
	java BayesNet good well .234 good-well.train good-well.test 
bTheir:	all
	java BayesNet their there .234 their-there.train their-there.test 

clean:
	rm -f *~ \#* *.class
