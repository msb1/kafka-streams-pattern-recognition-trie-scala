
package Models

import scala.collection.mutable

object Trie {

  case class TrieNode(letter: Char, word: String, child: mutable.HashMap[Char, TrieNode])

  val root: TrieNode = TrieNode(' ', "", new mutable.HashMap[Char, TrieNode]())
  var maxWordLength = 0

  def add(word: String): Unit = {
    var node = root
    var letterCtr = 0
    for(ch <- word.toLowerCase) {
      letterCtr += 1
      // update max word length
      if(word.length > maxWordLength) maxWordLength = word.length
      // if current letter is not in child trie map, add node
      if(!(node.child isDefinedAt ch)) {
        if(letterCtr == word.length) {
          node.child += (ch -> TrieNode(ch, word.toLowerCase, new mutable.HashMap[Char, TrieNode]()))
        } else {
          node.child += (ch -> TrieNode(ch, "", new mutable.HashMap[Char, TrieNode]()))
        }
      }
      // set current node to child with character key
      node = node.child(ch)
    }
  }

  var buffer = ""
  // helper method to recursively search dictionary trie
  def findWord(ch: Char): String = {
    // add char to end of buffer and remove first char to preserve buffer size
    if (buffer.length == maxWordLength) {
      buffer = buffer.substring(1) + ch
    } else {
      buffer += ch
    }
    // recursive search for buffer
    find(buffer, root)
  }

  // recursive method to search for word in Trie; return on end of string or when no further node in Trie
  def find(str: String, node: TrieNode): String = {
    if (str.length == 0) node.word
    else if (node.child isDefinedAt str(0)) {
      if(node.word == "") {
        find(str.substring(1), node.child(str(0)))
      } else {
        node.word + "," + find(str.substring(1), node.child(str(0)))
      }
    } else {
      node.word
    }
  }
}

