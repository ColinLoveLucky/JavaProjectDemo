package com.test.basictype;

public class StringClass {

	public void testJoin() {
		String a = "aa";
		String b = "bb";
		String c = 11 + "YY" + a + "ZZ" + "MM" + b;
		System.out.println(c);
		String e = "11" + "bb";
		System.out.println(e);
		String f = a + b;
		System.out.println(f);
	}

	public void testConstCache() {
		String a = "abc";
		String b = "abc";
		System.out.println(a.equals(b));
	}

	public void testEqual() {
		String a = "Hello";
		System.out.println(a == a.intern());
		String b = new String("corn");
		String c = b.intern();
		System.out.println(b == c);
	}

	public void testUnBox() {
		int il = 100;
		Integer il3 = il;
		Integer il4 = new Integer(4);
		int il2 = il4;

		// Integer 比较是引用的比价，但是Integer比较特殊-17~128 会用到IntegerCache
		// Integer 因为是装箱比较
		Integer ilMin100 = 100;// autoBoxing
		Integer ilMin1118 = 100;// autoBoxing
		System.out.println(String.format("Integer compare:%s", ilMin100 == ilMin1118));

		Integer bigger128 = 130;// autoBoxing
		Integer bigger129 = 140;// autoBoxing
		System.out.println(String.format("Integer Bigger Compare:%s", bigger128 == bigger129));

		// no autoboxing
		int i8 = 875;
		int i9 = 875;
		System.out.println(String.format("Integer Biggerint Compare:%s", i8 == i9));

		Integer one = new Integer(1); // no autoboxing
		Integer anotherOne = new Integer(1);// no autoboxing
		System.out.println("one == anotherOne : " + (one == anotherOne)); // false

		//noboxing
		int iValue = 1;
		String sValue = String.valueOf(iValue);
		System.out.println("iValue cast to String:" + sValue);
	}
}
