package org.project.utils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class A {

	public static void main(String[] args) {
		System.out.println(false || false);
		System.out.println(false || true);
		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		map.put(3, 3);
		map.put(1, 1);
		map.put(2, 2);
		print(map);
		map.put(4, 4);
		if (map.size() > 3) {
			map.remove(map.firstKey());
		}
		print(map);
		print(map.descendingMap());
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(3);
		set.add(1);
		set.add(2);
		print(set);
		set.add(4);
		if (set.size() > 3) {
			set.remove(set.first());
		}
		print(set);
		print(set.descendingSet());
	}
	
	public static <T> void print(Set<T> set) {
		System.out.println("-------------------------");
		for(T t : set) {
			System.out.println("s:" + t);
		}
	}
	
	public static <T, F> void print(Map<T, F> map) {
		System.out.println("-------------------------");
		for(Map.Entry<T, F> entry : map.entrySet()) {
			System.out.print("k:" + entry.getKey() + "-");
			System.out.println("v:" + entry.getValue());
		}
	}
}
