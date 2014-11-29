package org.project.modules.clustering.dbscan.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.project.modules.clustering.dbscan.data.Point;
import org.project.utils.http.HttpClientUtils;
import org.project.utils.http.HttpUtils;

public class HtmlDBScanBuilder {
	
	//半径
	public static double Epislon = 15;
	//密度、最小点个数
	public static int MinPoints = 5;
	
	private Map<String, String> map = new HashMap<String, String>();
	
	public List<Point<Integer>> extractPointsFromHtml() {
		String url = "http://ent.163.com/14/1123/11/ABNVIR5U000300B1.html";
		String content = HttpClientUtils.get(url, HttpUtils.ENCODE_GB2312);
		String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
		String regEx_noscript = "<[\\s]*?noscript[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?noscript[\\s]*?>";
        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; 
        String regEx_iframe = "<[\\s]*?iframe[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?iframe[\\s]*?>";
        String regEx_comment = "<!--[\\s\\S]*?-->";
        String regEx_doctype = "<!DOCTYPE[\\s\\S]*?>";
//        String regEx_cn = ">[\\s\\w]*[《a-zA-Z0-9\u4E00-\u9FA5]+\\w*\\W*</";
        String regEx_tag1 = "<([^>]*)>";
        String regEx_tag2 = "[&lt;&gt;&quot;&amp;]";
        String regEx_tag3 = "[\\.\\-\\:\\{\\}\\#\\%\\>a-zA-Z]*";
        Set<String> regExs = new HashSet<String>();
        regExs.add(regEx_script);
        regExs.add(regEx_noscript);
        regExs.add(regEx_style);
        regExs.add(regEx_iframe);
        regExs.add(regEx_comment);
        regExs.add(regEx_doctype);
        regExs.add(regEx_tag1);
        regExs.add(regEx_tag2);
        regExs.add(regEx_tag3);
        for (String regEx : regExs) {
        	 Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
             Matcher matcher = pattern.matcher(content);
             content = matcher.replaceAll("");
        }
        List<Point<Integer>> points = new ArrayList<Point<Integer>>();
        int index = 0;
        String[] lines = content.split("\n");
        for (int i = 0, len = lines.length; i < len; i++) {
        	String line = lines[i].trim();
        	if (null == line || "".equals(line)) continue;
        	map.put(String.valueOf(index), line);
        	points.add(new Point<Integer>(index++, line.length()));
        }
        return points;
	}
	
	//计算两点之间的欧氏距离
	public double euclideanDistance(Point<Integer> a, Point<Integer> b) {
		double sum =  Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
		return Math.sqrt(sum);
	}
	
	//获取当前点的邻居
	public List<Point<Integer>> obtainNeighbors(Point<Integer> current, List<Point<Integer>> points) {
		List<Point<Integer>> neighbors = new ArrayList<Point<Integer>>();
		for (Point<Integer> point : points) {
			double distance = euclideanDistance(current, point);
//			System.out.println("distance: " + distance);
			if (distance < Epislon) {
				neighbors.add(point);
			}
		}
		return neighbors;
	}
	
	public void mergeCluster(Point<Integer> point, List<Point<Integer>> neighbors,
			int clusterId, List<Point<Integer>> points) {
		point.setClusterId(clusterId);
		for (Point<Integer> neighbor : neighbors) {
			//邻域点中未被访问的点先观察是否是核心对象
			//如果是核心对象，则其邻域范围内未被聚类的点归入当前聚类中
			if (!neighbor.isAccessed()) {
				neighbor.setAccessed(true);
				List<Point<Integer>> nneighbors = obtainNeighbors(neighbor, points);
				if (nneighbors.size() > MinPoints) {
					for (Point<Integer> nneighbor : nneighbors) {
						if (nneighbor.getClusterId() <= 0) {
							nneighbor.setClusterId(clusterId);
						}
					}
				}
			}
			//未被聚类的点归入当前聚类中
			if (neighbor.getClusterId() <= 0) {
				neighbor.setClusterId(clusterId);
			}
		}
	}
	
	public void cluster(List<Point<Integer>> points) {
		//clusterId初始为0表示未分类,分类后设置为一个正数,如果设置为-1表示噪声 
		int clusterId = 0;
		boolean flag = true;
		//所有点都被访问完成即停止遍历
		while (flag) {
			for (Point<Integer> point : points) {
				if (point.isAccessed()) {
					continue;
				}
				point.setAccessed(true);
				flag = true;
				List<Point<Integer>> neighbors = obtainNeighbors(point, points);
				System.out.println("neighbors: " + neighbors.size());
				if (neighbors.size() >= MinPoints) {
					//满足核心对象条件的点创建一个新簇
					clusterId = point.getClusterId() <= 0 ? (++clusterId) : point.getClusterId();
					System.out.println("--clusterId: " + clusterId);
					mergeCluster(point, neighbors, clusterId, points);
				} else {
					//未满足核心对象条件的点暂时当作噪声处理
					if(point.getClusterId() <= 0) {
						 point.setClusterId(-1);
					}
				}
				flag = false;
			}
		}
	}
	
	//打印结果
	public void print(List<Point<Integer>> points) {
		Collections.sort(points, new Comparator<Point<Integer>>() {
			@Override
			public int compare(Point<Integer> o1, Point<Integer> o2) {
				return Integer.valueOf(o1.getClusterId()).compareTo(o2.getClusterId());
			}
		});
		for (Point<Integer> point : points) {
			System.out.println(point.getClusterId() + " - " + point + " - "
					+ map.get(String.valueOf(point.getX())));
		}
	}
	
	public void run() {
		List<Point<Integer>> points = extractPointsFromHtml();
		cluster(points);
		print(points);
	}
	
	public static void main(String[] args) {
		new HtmlDBScanBuilder().run();
	}
	
}