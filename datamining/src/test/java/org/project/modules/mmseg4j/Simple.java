package org.project.modules.mmseg4j;

import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;
import java.io.IOException;

public class Simple extends Complex {
	
	protected Seg getSeg() {
		return new SimpleSeg(this.dic);
	}

	public static void main(String[] args) throws IOException {
		new Simple().run(args);
	}
}
