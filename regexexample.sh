#!bin/sh

# prints help
echo "==================regex================"
echo
echo help 
echo
target/appassembler/bin/ami2-regex
echo
rm -rf target/regextest/
cp -R examples/ target/regextest
echo
echo "running regex on regextest"
echo
target/appassembler/bin/ami2-regex -q target/regextest/ --context 25 40 --r.regex regex/consort0.xml 
echo
echo "results in QSN"
echo
ls -ltR target/regextest/*/results/regex/consort0/
echo


