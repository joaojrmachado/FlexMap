#scrip para testa de benchs de saida do FlexMap
FILES=OUT-aig_bench_iscas/*Area.eqn
for f in $FILES
do
	echo "read_eqn ../NetBeansProjects/FlexMap/"$f  >> saidaCecAbc
        echo "short_names"  >> saidaCecAbc
	echo "write_eqn ../"${f:0:lenght-4}"Abc.eqn"  >> saidaCecAbc
	echo "read_eqn ../NetBeansProjects/FlexMap/"${f:0:lenght-8}"Eqn.eqn"  >> saidaCecAbc
	echo "short_names"  >> saidaCecAbc
	echo "write_eqn ../"${f:0:lenght-8}"EqnAbc.eqn"  >> saidaCecAbc
	echo "cec ../"${f:0:lenght-8}"EqnAbc.eqn ../"${f:0:lenght-4}"Abc.eqn" >> saidaCecAbc
        echo " " >> saidaCecAbc 
	echo " " >> saidaCecAbc
	echo " " >> saidaCecAbc	
done
FILES=OUT-iscas_bench_aig_II/*Area.eqn
for f in $FILES
do
	echo "read_eqn ../NetBeansProjects/FlexMap/"$f  >> saidaCecAbc
        echo "short_names"  >> saidaCecAbc
	echo "write_eqn ../"${f:0:lenght-4}"Abc.eqn"  >> saidaCecAbc
	echo "read_eqn ../NetBeansProjects/FlexMap/"${f:0:lenght-8}"Eqn.eqn"  >> saidaCecAbc
	echo "short_names"  >> saidaCecAbc
	echo "write_eqn ../"${f:0:lenght-8}"EqnAbc.eqn"  >> saidaCecAbc
	echo "cec ../"${f:0:lenght-8}"EqnAbc.eqn ../"${f:0:lenght-4}"Abc.eqn" >> saidaCecAbc
        echo " " >> saidaCecAbc 
	echo " " >> saidaCecAbc
	echo " " >> saidaCecAbc	
done
#cd 
#cd abc
#rodando no abc 
#./abc -F ../NetBeansProjects/aig/saidaCecABS
