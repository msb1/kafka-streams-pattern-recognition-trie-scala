<h4>Kafka Streams Pattern Recognition with Recursive Trie traversal in Scala</h4>
<ul>
<li>Read in word dictionary from file</li>
<li>Build Trie structure with words from dictionary (also be done as a Kafka stream in a private implementation)</li>
<li>Generate character stream with Alpakka Kafka Producer by randomly choosing words from dictionary and adding random character strings between words of random length </li>
<li>Consume characters into Kafka Kstream and add to buffer of fixed length equal to longest word in the dictionary (and longest path to traverse in Trie)</li>
<li>Recursively traverse Trie with current character string in buffer; return dictionary words if found</li>
<li>With each character in streaming data, add to beginning of buffer and remove last character in buffer to preserve fixed size</li>
</ul>
