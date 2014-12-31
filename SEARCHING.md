# SEARCHING and TAGGING

AMI can search NHTML or XML documents either completely or restricted to a set of tagged sections. There are 3 examples in 
https://github.com/ContentMine/ami/blob/master/src/test/java/org/xmlcml/ami/tagger/TaggingTest.java which show the approach. If you have a development environment (e.g. Eclipse) you can run the tests under JUnit.

## input 

The input file is a (prenormalized) PLoSONE XML file, https://github.com/ContentMine/ami/blob/master/src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.tagged.xml . The first few lines include:

```
<article article-type="research-article" dtd-version="3.0" xml:lang="en" tag="article" xmlns:mml="http://www.w3.org/1998/Math/MathML" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<front>
<journal-meta>
<journal-title xml:lang="en" tag="journaltitle">PLoS ONE</journal-title></journal-title-group>
<issn pub-type="epub" tag="issn">1932-6203</issn>
</journal-meta>
<article-meta>
<article-id pub-id-type="doi" tag="doi">10.1371/journal.pone.0113556</article-id>
```

... and later

```
<abstract tag="abstract">
<p>Echium (<italic>Echium plantagineum</italic> L.) is an alternative oilseed crop in summer-wet temperate regions that provides floral resources to pollinators. ...  corn (<italic>Zea mays</italic> L.).</p>
</abstract>
<funding-group tag="funding_group"><funding-statement>This work was supported through the USDA-National Institute of Food and Agriculture (NIFA) award 2012-67009-20272. The funder had no role in study design, data collection and analysis, decision to publish, or preparation of the manuscript.</funding-statement></funding-group>
```

Notice the tags such as ``tag="article"`` or  ``tag="funding_group"`` which are automatically added by Norma (who knows about PLoSONE). We are using a standard vocabulary across all publishers we support. So it's straightforward to restrict searches to (say) the "funding_group" paragraph.

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

The examples can be run from the program 

```
String[] args = new String[] {
"-input", "src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.tagged.xml",
"-regex", "regex/agriculture.xml",
};
RegexVisitor.main(args);
```

or for most users from the commandline in the Debian environment 

```
regex -input src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.tagged.xml \
      -regex regex/agriculture.xml
```

The first line tells the ``regex`` plugin to search a given file (The PLoSONE paper), and the second which regex file to use. The output (which can be configured) here has the default:

```
 WROTE target/journal.pone.0113556.tagged.xml/results.xml
```

## output

The Regex Plugin has searched the whole visible content of the file (including the abstract, references and publisher material).  The file ``results.xml`` looks something like:

```
<resultsList xmlns="http://www.xml-cml.org/ami">
	<resultsX>
		<source name="10.1371/journal.pone.0113556" />
		<result>
			<regex>
				<regex xmlns="" weight="1.0" fields="[flower, pre, word, post]">
					<pattern>((.{1,50})([Ff]lower)\s+(.{1,50}))</pattern>
				</regex>
				<hits>
					<hit
						flower="ing (early-sown) or early summer (late-sown), and flower abundance, pollinator visitation, and seed yields "
						pre="ing (early-sown) or early summer (late-sown), and " word="flower"
						post="abundance, pollinator visitation, and seed yields " />
					<hit
						flower="ntensity of visitation by pollinators. Cumulative flower densities ranged from 1 to 4.5 billion ha−1. Flowe"
						pre="ntensity of visitation by pollinators. Cumulative " word="flower"
						post="densities ranged from 1 to 4.5 billion ha−1. Flowe" />
					<hit
						flower=" we were able to document extraordinary levels of flower production and pollinator visitation, as well as p"
						pre=" we were able to document extraordinary levels of " word="flower"
						post="production and pollinator visitation, as well as p" />
```

It's found about 12 examples of the word "flower" and here reports both the complete context (``flower``) with the exact match (``word``) and the ``pre`` and ``post`` strings. This XML (or perhaps JSON soon) can be stored in a database or further processed.

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

