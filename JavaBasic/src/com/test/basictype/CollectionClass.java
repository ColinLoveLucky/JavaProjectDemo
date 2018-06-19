package com.test.basictype;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

public class CollectionClass {

	public void testArray() {
		// ArrayList arrayList = new ArrayList();
		// arrayList.add(1);
		// arrayList.add("Hi");
		// for (Object item : arrayList) {
		// System.out.println(item.toString());
		// List a = new ArrayList();
	}

	public void testList() {

		List<String> list = new ArrayList<String>();
		list.add("Hi");
		list.add("Zhangsan");
		for (String item : list) {
			System.out.println("list Item:" + item);
		}
		LinkedList<String> listLinkList = new LinkedList<String>();
		listLinkList.add("Hi");
		listLinkList.add("Zhangsan");

		Vector<String> vector = new Vector<String>();
		vector.add("Hi");

		Stack<String> stack = new Stack<String>();
	}

	public void testQueue() {
		PriorityQueue<String> queue = new PriorityQueue<String>();

		ArrayDeque<String> deque = new ArrayDeque<String>();
	}

	public void testSet() {
		HashSet<String> hashSet = new HashSet<String>();
		hashSet.add("Hi");
		hashSet.add(null);
		for (String item : hashSet) {
			System.out.println("Item :" + item);
		}

		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
		linkedHashSet.add("Key");

		TreeSet<String> treeSet = new TreeSet<String>();
		treeSet.add("Hi");
		treeSet.add("Body");
		for (String item : treeSet) {
			System.out.println("Item is:" + item);
		}

		// EnumSet
	}

	public void testHashTable() {
		Hashtable<String, String> hash = new Hashtable<String, String>();
		hash.put("hi", "hi");
		hash.put("Hi", "HI");
		System.out.println("Hi:" + hash.get("Hi"));
		System.out.println("hi:" + hash.get("Hi"));
	}

	public void testHashMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Hi", "Hi");

		LinkedHashMap<String, String> linkHashMap = new LinkedHashMap<String, String>();
		linkHashMap.put("Hi", "Value");

		TreeMap<String, String> sortMap = new TreeMap<String, String>();

		IdentityHashMap<String, String> identityMap = new IdentityHashMap<String, String>();

		// EnumMap<String,String> enumMap=new EnumMap<String,String>();
	}

	public void testWeakHashMap() {
		WeakHashMap<String, String> weakHash = new WeakHashMap<String, String>();
		weakHash.put("Hi", "HI");
		weakHash.put("Hello", "Hello");

	}

}
