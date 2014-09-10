package org.project.modules.classifier.svm;

import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.project.modules.classifier.svm.libsvm.svm_train;

public class SVM {

	public static void main(String[] args) throws IOException {
		String[] trainArgs = {"-t", "0", "-c", "50","-h", "0","F:/train.txt"};
        String modelfile = svm_train.main(trainArgs);
        System.out.println(modelfile);
        svm_model model = svm.svm_load_model("tra.txt.model");
        svm_node[] x = new svm_node[6];
        x[0] = new svm_node();
        x[0].index = 1;
        x[0].value = 0;
        double v = svm.svm_predict(model,x);
        System.out.println(v);
	}
}
