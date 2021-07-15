package constraintBench.test.loops;

import java.util.Random;

import constraintBench.utils.Configuration;
import constraintBench.utils.Property;
/**
 * 
 * @author Linghui Luo
 */
public class Loop6 {

	public void test() {
		test1();
		test2();
		test3();
	}

	public void test1() {
		Property a = new Property();
		while (new Random().nextBoolean()) {
			Property b = new Configuration().featureP();
			b.f = a;
			a = b;
		}
		Property c = a.f;
		if (c.isFeatureEnable())
			System.out.println();
	}

	public void test2() {
		Property a = new Property();
		while (new Random().nextBoolean()) {
			Property x = new Configuration().featureQ();
			a.f = x;
			x = a;
		}
		Property c = a.f;
		if (c.isFeatureEnable())
			System.out.println();
	}

	public void test3() {
		Property a = new Property();
		Property c = new Property();
		Property b = new Property();
		while (new Random().nextBoolean()) {
			a = new Configuration().featureQ();
			c.f = a;
			a = c;
			a.g = b;
			b = a;
		}
		if (c.f.g.f.isFeatureEnable())
			System.out.println();

	}
}
