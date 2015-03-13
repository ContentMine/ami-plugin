#!bin/sh

# prints help
echo "==================words================"
echo
echo help 
echo
target/appassembler/bin/ami2-words
echo
rm -rf target/wordstest/
cp -R examples/ target/wordstest
echo
echo "running words on wordstest"
echo
target/appassembler/bin/ami2-words -q target/wordstest/ --w.words wordFrequencies  
echo
echo "results in QSN"
echo
ls -ltR target/wordstest/*/results/words/frequencies
echo


