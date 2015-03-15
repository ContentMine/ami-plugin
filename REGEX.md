# regex


## regex plugin

There are many plugins but we expect the Regex  Plugin to be one of the most popular. Here we create a set of expressions (terms) that we wish to search for. Here we use a small subset of Rory Aaronson's OpenFarm terms. See https://github.com/ContentMine/ami/blob/master/regex/agriculture.xml .

Here are a few from the file. 
```
<compoundRegex title="agriculture">
<regex weight="1.0" fields="flower">([Ff]lower)</regex>
<regex weight="1.0" fields="deeproot">([Dd]eep\s+[Rr]oot)</regex>
<regex weight="1.0" fields="sunlight amount sunshade">(([Ff]ull|[Pp]artial)\s+([Ss]hade|[Ss]un))</regex>
...
```
It's not as forbidding as it looks! The first regex finds examples of "flower" or "Flower" and puts the result in a variable called "flower". The second looks for 2 words in order ("Deep root"). Since there might be several spaces, or tabs, or newlines, we use ``\s+`` which means "one or more whitespace characters. The third allows for any combination of ``{Full or partial} with {Shade or sun}`` - it saves us writing 4 experessions. And the results is captured in ``sunlight``, ``amount`` and ``sunshade`` which we can use later on.  We shall expand the regexes to report the text either side - here up to 50 characters if available
```
<regex weight="1.0" fields="flower pre word post">((.{1,50})([Ff]lower)\s+(.{1,50}))</regex>
```
(We hope to generate this automatically so you don't have to read it).

## running the examples

The examples can be run from the commandline in the Debian environment 

```
ami2-regex -q examples/http_www.trialsjournal.com_content_16_1_1 \
		-i scholarly.html \
		-o results.xml \
		--context 25 40 \
		--r.regex regex/consort0.xml
      
```

This tells the `ami2-regex` plugin to search a given directory and to search the `scholarly.html`. `--context` gives the textual context of the results - 25 characters on the left and 40 on the right. `--r.regex` selectes the regex file to use. The output (which can be configured) here has the default:

```
 WROTE examples/http_www.trialsjournal.com_content_16_1_1/results/regex/consort0/results.xml
```

## output

The Regex Plugin has searched the whole visible content of the file (including the abstract, references and publisher material).  The file ``results.xml`` looks something like:

```
<results title="consort0">
 <result pre=") database (31 participants). Six other " name0="participants" value0="participants" post="were recruited via a variety of other co"/>
 <result pre="ctive than those who were recruited via " name0="follow-up" value0="follow-up" post="telephone calls. No other demographics o"/>
 <result pre="on time and intensity invested.Only six " name0="participants" value0="participants" post="were recruited through a wide variety of"/>
 <result pre="rt. Qualitative feedback indicated that " name0="participants" value0="participants" post="had been attracted by the prospect of su"/>
 <result pre="nvite the target population, and use of " name0="follow-up" value0="follow-up" post="telephone calls to explain the study met"/>
```


It's found many examples of the word "participants" and here reports both the complete context (`flower`) with the exact match (`value0`) and the `pre` and `post` strings. This looks a little verbose, but the name could relate to a number of values and there could be several names (`name1`, `name2` ...) if there were several cpature groups (brackets)

## restriction to named sections

There is a very powerful language, XPath, for finding parts of XML documents. Here we restrict it to just those sections which we have tagged  (we've abbreviated ``input`` and ``regex`` to single-character mnemonics)
```
regex -i src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.tagged.xml \
      -r regex/agriculture.xml \
      -x //*[@tag='abstract']
```

The XPath expression says "search for any section (``*``) which has a ``tag`` with the value ``abstract``". Norma, who knows how to read PLoSONE articles, has already tagged the Abstract so we don't have to worry.  Now the search will only search the abstract. The result is now only two hits and we know where they came from.

Similarly if we want Figures whose captions refer to flowers we can write:
```
      -x //*[@tag='figure']
```

Since XPath is a rich language we can also restrict searches, such as 
```
      -x //*[@tag and not(@tag='references')]
```

Read "search all tagged sections except the References". 

## enhancements

We will shortly add XPath expressions showing where any hit comes form (this is already done in the Species Plugin).

