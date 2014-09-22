package org.project.common;

import java.util.ArrayList;
import java.util.List;

import org.project.utils.FileUtils;
import org.project.utils.ShowUtils;
import org.project.utils.WordUtils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;

public class Test {
	
	public static void a() {
		List<P> p1s = new ArrayList<P>();
		p1s.add(new P(1.0, 1.0, "one"));
		p1s.add(new P(2.0, 2.0, "two"));
		p1s.add(new P(3.0, 3.0, "three"));
		List<P> p2s = new ArrayList<P>();
		for (P p  : p1s) {
			p2s.add(p);
		}
		for (P p : p2s) {
			System.out.println("p2s: " + p.getX() + "-" + p.getY() + "-" + p.getCategory());
		}
	}
	
	public static void b() {
		String path = "D:\\resources\\data\\enter\\1.txt";
		String content = FileUtils.readContent(path);
		ShowUtils.printToConsole(WordUtils.split(content));
		System.out.println(WordUtils.split(content).length);
		ShowUtils.printToConsole(WordUtils.split(content, new ComplexSeg(Dictionary.getInstance())));
		System.out.println(WordUtils.split(content, new ComplexSeg(Dictionary.getInstance())).length);
	}

	public static void main(String[] args) {
		b();
	}
}

class P {
	
	private Double x = null;
	
	private Double y = null;
	
	private String category = null;
	
	public P(double x, double y, String category) {
		this.x = new Double(x);
		this.y = new Double(y);
		this.category = new String(category);
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


}