#!bin/sh

# example 1
# prints help (no arguments)
echo "==================words================"
echo
echo help 
echo
target/appassembler/bin/ami2-words
echo

# example 1
# runs ami-words on a small number of files 
# for tutorials we copy the files to a temporary directory so as not to overwrite them by mistake
# this calculates the frequencies of words
rm -rf target/wordstest/
cp -R examples/ target/wordstest
echo
echo "==========running ami2-words to get frequencies"
echo
target/appassembler/bin/ami2-words -q target/wordstest/ --w.words wordFrequencies  
echo
echo "results in QSN"
echo
# the results are written into the directories
# thus target/wordstest/http_www.trialsjournal.com_content_16_1_19/ should have a new directory
# http_www.trialsjournal.com_content_16_1_19/results/words/frequencies which will contain
#   results.xml (machine-readable)
#   results.html (human-readable)
# inspect http_www.trialsjournal.com_content_16_1_19/results/words/frequencies/results.html for a word cloud
# and list all the files we have produced

ls -ltR target/wordstest/*/results/words/frequencies
echo
rm -rf target/wordstest/
cp -R examples/ target/wordstest
echo
echo "==========running ami2-words to get summary frequencies"
echo
echo "there are a number of options here..."
echo "--w.stopwords applies lists of words to be omitted from the analysis"
echo "    stopwords.txt is a list of 335 common english words"
echo "    clinical200.txt is a list of the 200 next commonest words we found in trialsjournal.com"
echo "--w.case ignore  will ignore case"
echo "--w.summary denotes the concept to summarise on (boolean Frequency = 1 if word is in a document, else 0)"
echo "--summaryfile the directory to summarize in"
echo "--w.mincount frequencies below this will be ignored (not sure it's working yet)"
echo
target/appassembler/bin/ami2-words \
-q target/wordstest/ --w.words wordFrequencies  \
--w.stopwords \
    /org/xmlcml/ami2/plugins/words/stopwords.txt \
	/org/xmlcml/ami2/plugins/words/clinical200.txt \
--w.case ignore \
--w.summary booleanFrequency \
--summaryfile target/examples \
--w.mincount 3
echo
echo "results are written to each results/words/frequencies/ directory and to the summaryfile"
echo
# the results are written into the directories
# thus target/wordstest/http_www.trialsjournal.com_content_16_1_19/ should have a new directory
# http_www.trialsjournal.com_content_16_1_19/results/words/frequencies which will contain
#   results.xml (machine-readable)
#   results.html (human-readable)
# inspect http_www.trialsjournal.com_content_16_1_19/results/words/frequencies/results.html for a word cloud
# and list all the files we have produced

ls -ltR target/wordstest/*/results/words/frequencies
echo


