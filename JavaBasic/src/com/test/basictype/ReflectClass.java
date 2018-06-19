package com.test.basictype;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectClass {

	private String str1;

	public void testReflect() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String testStr = "goodleiwei";
		Class<?> getClassWay1 = testStr.getClass();
		Class<?> getClassWay2 = String.class;
		Class<?> getClassWay3 = Class.forName("java.lang.String");
		System.out.println("getClassWay1 == getClassWay2" + (getClassWay1 == getClassWay2));
		System.out.println("getClassWay2 == getClassWay3" + (getClassWay2 == getClassWay3));

		str1 = (String) Class.forName("java.lang.String").newInstance();

		try {
			String str2 = (String) Class.forName("java.lang.String").getConstructor(StringBuffer.class)
					.newInstance(new StringBuffer("abc"));
			System.out.println(str2.charAt(2));
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		User user2 = new User(3, 5);
		try {
			Field fieldX = user2.getClass().getField("x");
			System.out.println("fieldX:" + fieldX.get(user2));
			Field fieldY = user2.getClass().getDeclaredField("y");
			fieldY.setAccessible(true);
			System.out.println("FieldY:" + fieldY.get(user2));
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		User user = user2;
		changeStringValue(user);
		System.out.println("User" + user);

	}

	private static void changeStringValue(final Object obj) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = obj.getClass().getFields();
		for (Field field : fields) {
			if (field.getType() == String.class) {
				String oldStrValue = (String) field.get(obj);
				String newStrValue = oldStrValue.replace('s', 'H');
				field.set(obj, newStrValue);
			}
		}
	}

	class User {
		public String name1 = "zhangsan";
		public String name2 = "lisi";
		public String name3 = "wangwu";

		public int x;
		private int y;

		public User() {

		}

		public User(int x, int y) {
			super();
			this.x = x;
			this.setY(y);
		}

		@Override
		public String toString() {
			return this.name1 + ":" + this.name2 + ":" + this.name3;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

	}
}
