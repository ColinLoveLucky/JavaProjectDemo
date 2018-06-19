package com.gc.action;

import java.util.Locale;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author XianggangZhang
 *
 */
public class Test {

	private static ApplicationContext context;

	public static void main(String[] args) {
		context = new FileSystemXmlApplicationContext("config.xml");
		HelloWorld helloworld = (HelloWorld) context.getBean("HelloWorld");
		System.out.println(helloworld.getMsg());
		System.out.println(helloworld.getDate());

		Hello hello = context.getBean("Hello", Hello.class);
		System.out.println(hello.doSalutation());

		ConstructorIoc constructorioc = context.getBean("constructorIoc", ConstructorIoc.class);
		System.out.println(constructorioc.getMsg());

		HelloWorld helloworldWrapper = new HelloWorld();
		BeanWrapper bw = new BeanWrapperImpl(helloworldWrapper);
		bw.setPropertyValue("msg", "hello");
		System.out.println(bw.getPropertyValue("msg"));

		// String path = "file:D:/JavaProject/Spring5Web/config.xml";
		String path = "config.xml";
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(path);
		HelloWorld helloContext = ctx.getBean("HelloWorld", HelloWorld.class);
		System.out.println(helloContext.getDate());
		ctx.registerShutdownHook();

		HelloList helloList = context.getBean("HelloList", HelloList.class);
		for (String item : helloList.getMsg()) {
			System.out.println("item:" + item);
		}

		HelloSet helloset = context.getBean("HelloSet", HelloSet.class);
		for (String item : helloset.getMap()) {
			System.out.println("item:" + item);
		}

		HelloMap map = context.getBean("HelloMap", HelloMap.class);
		map.getMap().forEach((k, v) -> System.out.println("Item : " + k + " Count : " + v));

		String content = context.getMessage("customer.name", new Object[] { 28, "http://www.baidu.com" }, Locale.US);
		System.out.println(content);

		Log log = context.getBean("log", Log.class);
		log.log("gf");

		TimeBookInterface timeProxy = context.getBean("logProxy", TimeBookInterface.class);
		timeProxy.doAuditing("Colin");

		FinanceInterface finace = context.getBean("logProxy1", FinanceInterface.class);
		finace.doCheck("colin");

		TimeBookInterface timeProxyBefore = context.getBean("logProxy2", TimeBookInterface.class);
		timeProxyBefore.doAuditing("Lucky");
	}
}
