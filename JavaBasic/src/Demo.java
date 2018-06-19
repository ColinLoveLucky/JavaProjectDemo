import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.test.basictype.ArrayClass;
import com.test.basictype.ClassTest;
import com.test.basictype.CollectionClass;
import com.test.basictype.ColorEnum;
import com.test.basictype.DateClass;
import com.test.basictype.FruitEnum;
import com.test.basictype.GenericClass;
import com.test.basictype.MutipleThreadClass;
import com.test.basictype.ReflectClass;
import com.test.basictype.RunnableClass;
import com.test.basictype.SerializeClass;
import com.test.basictype.StringBufferClass;
import com.test.basictype.StringBuildClass;
import com.test.basictype.StringClass;
import com.test.basictype.WeekEnum;

public class Demo {
	public static void main(String[] args) throws ClassNotFoundException {

		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		for (int item : list) {
			System.out.println("list Item:" + item);
		}
		System.out.println(ColorEnum.BLANK);
		System.out.println(WeekEnum.MON.toString());
		System.out.println("ColorEnum ValueOf " + ColorEnum.valueOf("BLANK"));
		FruitEnum.APPLE.print();
		long data = -1024L;
		System.out.println(data);
		System.out.println(Long.SIZE);
		char charData = '1';
		System.out.println(charData);
		System.out.println(Character.SIZE);
		char charData1 = '天';
		System.out.println("chardata1:" + charData1);
		char charData2 = '中';
		System.out.println("chardata216:" + charData2);
		char charData3[] = { '1', '2' };
		System.out.println("Chardata3:" + charData3[0] + " 2:" + charData3[1]);
		float floatData = 12.32f;
		System.out.println(floatData);
		System.out.println(Float.SIZE);
		double doubleData = 12.32;
		System.out.println(doubleData);
		System.out.println(Double.SIZE);
		final double PI = 3.1415926;
		System.out.println(PI);
		int[] numbers = { 10, 20, 30, 40, 50 };
		for (int item : numbers) {
			System.out.println("item:" + item);
		}
		for (int index = 0; index < numbers.length; index++) {
			System.out.println("index Item:" + numbers[index]);
		}
		char grade = 'C';
		switch (grade) {
		case 'A':
			System.out.println("Excellent");
			break;
		case 'B':
			System.out.println("Good");
			break;
		case 'C':
			System.out.println("OK");
			break;
		case 'D':
		case 'E':
			System.out.println("less");
			break;
		default:
			System.out.println("Exception");
			break;
		}
		System.out.println("Exception");
		System.out.println("90 度正选值:" + Math.sin(90));
		String sValue = "123456";
		System.out.println(sValue);

		String a = new String("abc");
		String b = new String("abc");
		a.equals(b);

		StringClass stringClass = new StringClass();
		stringClass.testJoin();

		stringClass.testConstCache();
		stringClass.testEqual();

		stringClass.testUnBox();

		StringBuildClass stringBuilder = new StringBuildClass();
		stringBuilder.testBuild();

		StringBufferClass stringBuffer = new StringBufferClass();
		stringBuffer.testBuffer();

		ArrayClass arrayClass = new ArrayClass();
		arrayClass.testArray();

		DateClass date = new DateClass();
		date.testDate();

		ClassTest classTest = new ClassTest("hi");
		ClassTest.Builder buildClass = new ClassTest.Builder(12, "zhangsan");
		buildClass.TestBuild();

		CollectionClass collectionClass = new CollectionClass();
		collectionClass.testList();
		collectionClass.testHashTable();
		collectionClass.testSet();

		GenericClass<String> genericClass = new GenericClass<String>("HI");
		System.out.println("generic data" + genericClass.getData());

		MutipleThreadClass threadClass = new MutipleThreadClass();
		threadClass.start();

		RunnableClass runnableClass = new RunnableClass();
		runnableClass.run();

		SerializeClass serializeClass = new SerializeClass("Tom", 'M', 20, 3.6);
		File file = new File("D:\\Student.txt");

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(serializeClass);
			oos.flush();
			oos.close();
			fos.close();
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			SerializeClass st1 = (SerializeClass) ois.readObject();
			System.out.println("Name=" + st1.getName());
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ReflectClass reflectClass = new ReflectClass();
		try {
			reflectClass.testReflect();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 弱引用 强引用
		// SoftReference<String> softRef = new SoftReference<String>();
		// WeakReference
		// PhantomReference
		// 垃圾回收

		// GC 回收
		System.gc();

		// 异常的抛出

		// java 注解

		// 多线程异步线程

		// 动态代理

		// java 事件
	}

	public static String validInput(String name, Function<String, String> function) {
		return function.apply(name);
	}
}
