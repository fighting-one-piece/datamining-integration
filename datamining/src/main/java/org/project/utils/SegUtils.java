package org.project.utils;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;

public class SegUtils {

	public static Dictionary dic = Dictionary.getInstance();
	
	public static Seg getComplexSeg() {
		return new ComplexSeg(dic);
	}
	
}
