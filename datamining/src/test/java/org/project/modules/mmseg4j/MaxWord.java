package org.project.modules.mmseg4j;

import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Seg;
import java.io.IOException;

public class MaxWord extends Complex {
	
	protected Seg getSeg() {
		return new MaxWordSeg(this.dic);
	}

	public static void main(String[] args) throws IOException {
		new MaxWord().run(args);
	}
}
