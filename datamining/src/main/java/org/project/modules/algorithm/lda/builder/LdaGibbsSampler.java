package org.project.modules.algorithm.lda.builder;

public class LdaGibbsSampler {  
    /** 
     * document data (term lists) 
     */  
    int[][] documents;  
    /** 
     * vocabulary size 
     */  
    int V;  
    /** 
     * number of topics 
     */  
    int K;  
    /** 
     * Dirichlet parameter (document--topic associations) 
     */  
    double alpha;  
    /** 
     * Dirichlet parameter (topic--term associations) 
     */  
    double beta;  
    /** 
     * topic assignments for each word. 
     * N * M 维，第一维是文档，第二维是word 
     */  
    int z[][];  
    /** 
     * nw[i][j] number of instances of word i (term?) assigned to topic j. 
     */  
    int[][] nw;  
    /** 
     * nd[i][j] number of words in document i assigned to topic j. 
     */  
    int[][] nd;  
    /** 
     * nwsum[j] total number of words assigned to topic j. 
     */  
    int[] nwsum;  
    /** 
     * nasum[i] total number of words in document i. 
     */  
    int[] ndsum;  
    /** 
     * cumulative statistics of theta 
     */  
    double[][] thetasum;  
    /** 
     * cumulative statistics of phi 
     */  
    double[][] phisum;  
    /** 
     * size of statistics 
     */  
    int numstats;  
    /** 
     * sampling lag (?) 
     */  
    @SuppressWarnings("unused")
	private static int THIN_INTERVAL = 20;  
  
    /** 
     * burn-in period 
     */  
    private static int BURN_IN = 100;  
  
    /** 
     * max iterations 
     */  
    private static int ITERATIONS = 1000;  
  
    /** 
     * sample lag (if -1 only one sample taken) 
     */  
    private static int SAMPLE_LAG;  
  
    @SuppressWarnings("unused")
	private static int dispcol = 0;  
  
    /** 
     * Initialise the Gibbs sampler with data. 
     *  
     * @param V 
     *            vocabulary size 
     * @param data 
     */  
    public LdaGibbsSampler(int[][] documents, int V) {  
  
        this.documents = documents;  
        this.V = V;  
    }  
  
    /** 
     * Initialisation: Must start with an assignment of observations to topics ? 
     * Many alternatives are possible, I chose to perform random assignments 
     * with equal probabilities 
     *  
     * @param K 
     *            number of topics 
     * @return z assignment of topics to words 
     */  
	public void initialState(int K) {  
		@SuppressWarnings("unused")
        int i;  
  
        int M = documents.length;  
  
        // initialise count variables.  
        nw = new int[V][K];  
        nd = new int[M][K];  
        nwsum = new int[K];  
        ndsum = new int[M];  
  
        // The z_i are are initialised to values in [1,K] to determine the  
        // initial state of the Markov chain.  
        // 为了方便，他没用从狄利克雷参数采样，而是随机初始化了！  
  
        z = new int[M][];  
        for (int m = 0; m < M; m++) {  
            int N = documents[m].length;  
            z[m] = new int[N];  
            for (int n = 0; n < N; n++) {  
                //随机初始化！  
                int topic = (int) (Math.random() * K);  
                z[m][n] = topic;  
                // number of instances of word i assigned to topic j  
                // documents[m][n] 是第m个doc中的第n个词  
                nw[documents[m][n]][topic]++;  
                // number of words in document i assigned to topic j.  
                nd[m][topic]++;  
                // total number of words assigned to topic j.  
                nwsum[topic]++;  
            }  
            // total number of words in document i  
            ndsum[m] = N;  
        }  
    }  
  
    /** 
     * Main method: Select initial state ? Repeat a large number of times: 1. 
     * Select an element 2. Update conditional on other elements. If 
     * appropriate, output summary for each run. 
     *  
     * @param K 
     *            number of topics 
     * @param alpha 
     *            symmetric prior parameter on document--topic associations 
     * @param beta 
     *            symmetric prior parameter on topic--term associations 
     */  
    private void gibbs(int K, double alpha, double beta) {  
        this.K = K;  
        this.alpha = alpha;  
        this.beta = beta;  
  
        // init sampler statistics  
        if (SAMPLE_LAG > 0) {  
            thetasum = new double[documents.length][K];  
            phisum = new double[K][V];  
            numstats = 0;  
        }  
  
        // initial state of the Markov chain:  
        //启动马尔科夫链需要一个起始状态  
        initialState(K);  
  
        //每一轮sample  
        for (int i = 0; i < ITERATIONS; i++) {  
  
            // for all z_i  
            for (int m = 0; m < z.length; m++) {  
                for (int n = 0; n < z[m].length; n++) {  
  
                    // (z_i = z[m][n])  
                    // sample from p(z_i|z_-i, w)  
                    //核心步骤，通过论文中表达式（78）为文档m中的第n个词采样新的topic  
                    int topic = sampleFullConditional(m, n);  
                    z[m][n] = topic;  
                }  
            }  
  
            // get statistics after burn-in  
            //如果当前迭代轮数已经超过 burn-in的限制，并且正好达到 sample lag间隔  
            //则当前的这个状态是要计入总的输出参数的，否则的话忽略当前状态，继续sample  
            if ((i > BURN_IN) && (SAMPLE_LAG > 0) && (i % SAMPLE_LAG == 0)) {  
                updateParams();  
            }  
        }  
    }  
  
    /** 
     * Sample a topic z_i from the full conditional distribution: p(z_i = j | 
     * z_-i, w) = (n_-i,j(w_i) + beta)/(n_-i,j(.) + W * beta) * (n_-i,j(d_i) + 
     * alpha)/(n_-i,.(d_i) + K * alpha) 
     *  
     * @param m 
     *            document 
     * @param n 
     *            word 
     */  
    private int sampleFullConditional(int m, int n) {  
  
        // remove z_i from the count variables  
        //这里首先要把原先的topic z(m,n)从当前状态中移除  
        int topic = z[m][n];  
        nw[documents[m][n]][topic]--;  
        nd[m][topic]--;  
        nwsum[topic]--;  
        ndsum[m]--;  
  
        // do multinomial sampling via cumulative method:  
        double[] p = new double[K];  
        for (int k = 0; k < K; k++) {  
            //nw 是第i个word被赋予第j个topic的个数  
            //在下式中，documents[m][n]是word id，k为第k个topic  
            //nd 为第m个文档中被赋予topic k的词的个数  
            p[k] = (nw[documents[m][n]][k] + beta) / (nwsum[k] + V * beta)  
                * (nd[m][k] + alpha) / (ndsum[m] + K * alpha);  
        }  
        // cumulate multinomial parameters  
        for (int k = 1; k < p.length; k++) {  
            p[k] += p[k - 1];  
        }  
        // scaled sample because of unnormalised p[]  
        double u = Math.random() * p[K - 1];  
        for (topic = 0; topic < p.length; topic++) {  
            if (u < p[topic])  
                break;  
        }  
  
        // add newly estimated z_i to count variables  
        nw[documents[m][n]][topic]++;  
        nd[m][topic]++;  
        nwsum[topic]++;  
        ndsum[m]++;  
  
        return topic;  
    }  
  
    /** 
     * Add to the statistics the values of theta and phi for the current state. 
     */  
    private void updateParams() {  
        for (int m = 0; m < documents.length; m++) {  
            for (int k = 0; k < K; k++) {  
                thetasum[m][k] += (nd[m][k] + alpha) / (ndsum[m] + K * alpha);  
            }  
        }  
        for (int k = 0; k < K; k++) {  
            for (int w = 0; w < V; w++) {  
                phisum[k][w] += (nw[w][k] + beta) / (nwsum[k] + V * beta);  
            }  
        }  
        numstats++;  
    }  
  
    /** 
     * Retrieve estimated document--topic associations. If sample lag > 0 then 
     * the mean value of all sampled statistics for theta[][] is taken. 
     *  
     * @return theta multinomial mixture of document topics (M x K) 
     */  
    public double[][] getTheta() {  
        double[][] theta = new double[documents.length][K];  
  
        if (SAMPLE_LAG > 0) {  
            for (int m = 0; m < documents.length; m++) {  
                for (int k = 0; k < K; k++) {  
                    theta[m][k] = thetasum[m][k] / numstats;  
                }  
            }  
  
        } else {  
            for (int m = 0; m < documents.length; m++) {  
                for (int k = 0; k < K; k++) {  
                    theta[m][k] = (nd[m][k] + alpha) / (ndsum[m] + K * alpha);  
                }  
            }  
        }  
  
        return theta;  
    }  
  
    /** 
     * Retrieve estimated topic--word associations. If sample lag > 0 then the 
     * mean value of all sampled statistics for phi[][] is taken. 
     *  
     * @return phi multinomial mixture of topic words (K x V) 
     */  
    public double[][] getPhi() {  
        double[][] phi = new double[K][V];  
        if (SAMPLE_LAG > 0) {  
            for (int k = 0; k < K; k++) {  
                for (int w = 0; w < V; w++) {  
                    phi[k][w] = phisum[k][w] / numstats;  
                }  
            }  
        } else {  
            for (int k = 0; k < K; k++) {  
                for (int w = 0; w < V; w++) {  
                    phi[k][w] = (nw[w][k] + beta) / (nwsum[k] + V * beta);  
                }  
            }  
        }  
        return phi;  
    }  
  
    /** 
     * Configure the gibbs sampler 
     *  
     * @param iterations 
     *            number of total iterations 
     * @param burnIn 
     *            number of burn-in iterations 
     * @param thinInterval 
     *            update statistics interval 
     * @param sampleLag 
     *            sample interval (-1 for just one sample at the end) 
     */  
    public void configure(int iterations, int burnIn, int thinInterval,  
        int sampleLag) {  
        ITERATIONS = iterations;  
        BURN_IN = burnIn;  
        THIN_INTERVAL = thinInterval;  
        SAMPLE_LAG = sampleLag;  
    }  
  
    /** 
     * Driver with example data. 
     *  
     * @param args 
     */  
    public static void main(String[] args) {  
        // words in documents  
        int[][] documents = { {1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 6},  
            {2, 2, 4, 2, 4, 2, 2, 2, 2, 4, 2, 2},  
            {1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 0},  
            {5, 6, 6, 2, 3, 3, 6, 5, 6, 2, 2, 6, 5, 6, 6, 6, 0},  
            {2, 2, 4, 4, 4, 4, 1, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 0},  
            {5, 4, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2}};  
        // vocabulary  
        int V = 7;  
        int M = documents.length;  
        System.out.println(M);
        // # topics  
        int K = 2;  
        // good values alpha = 2, beta = .5  
        double alpha = 2;  
        double beta = .5;  
  
        LdaGibbsSampler lda = new LdaGibbsSampler(documents, V);  
          
        //设定sample参数，采样运行10000轮，burn-in 2000轮，第三个参数没用，是为了显示  
        //第四个参数是sample lag，这个很重要，因为马尔科夫链前后状态conditional dependent，所以要跳过几个采样  
        lda.configure(10000, 2000, 100, 10);  
          
        //跑一个！走起！  
        lda.gibbs(K, alpha, beta);  
  
        //输出模型参数，论文中式 （81）与（82）  
        double[][] theta = lda.getTheta();  
        double[][] phi = lda.getPhi();  
        System.out.println(theta);
        System.out.println(phi);
    }  
}  
