package bearmaps.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyTrieSet implements TrieSet61BL {

    private TrieNode root = new TrieNode();

    @Override
    public void clear() {
        root = new TrieNode();
    }

    @Override
    public boolean contains(String key) {
        TrieNode cur = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (cur.children.containsKey(c)) {
                cur = cur.children.get(c);
            } else {
                return false;
            }
        }
        return cur.endOfWord;
    }

    @Override
    public void add(String key) {
        TrieNode cur = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!cur.children.containsKey(c)) {
                cur.children.put(c, new TrieNode(c));
            }
            cur = cur.children.get(c);
        }
        cur.endOfWord = true;
    }

    @Override
    public List<String> keysWithPrefix(String prefix) {
        List<String> retList = new LinkedList<>();
        TrieNode cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (cur.children.containsKey(c)) {
                cur = cur.children.get(c);
            } else {
                return retList;
            }
        }
        if (cur.endOfWord) {
            retList.add(prefix);
        }
        for (TrieNode child : cur.children.values()) {
            retList.addAll(prefixHelper(child, prefix));
        }
        return retList;
    }

    private List<String> prefixHelper(TrieNode node, String prefix) {
        List<String> retList = new LinkedList<>();
        if (node.endOfWord) {
            retList.add(prefix + node.character);
        }
        for (TrieNode child : node.children.values()) {
            retList.addAll(prefixHelper(child, prefix + node.character));
        }
        return retList;
    }

    @Override
    public String longestPrefixOf(String key) {
        String retStr = "";
        TrieNode cur = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (cur.children.containsKey(c)) {
                retStr += c;
                cur = cur.children.get(c);
            } else {
                return retStr;
            }
        }
        return retStr;
    }

    private class TrieNode {
        private char character;
        private HashMap<Character, TrieNode> children = new HashMap<>();
        private boolean endOfWord = false;

        TrieNode() {
            character = '~';
        }

        TrieNode(char c) {
            character = c;
        }
    }

    public static void main(String[] args) {
        var mts = new MyTrieSet();
        mts.add("a");
        mts.add("ab");
        mts.add("abc");
        System.out.println(mts.keysWithPrefix("a"));
    }
}
