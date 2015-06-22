package org.project.modules.algorithm.recommend.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

//数据加载工具
public class DataLoader {

	private static Map<Long, User> userMap = null;
	
	private static Map<Long, Movie> movieMap = null;
	
	private static Map<Long, Map<Long, Integer>> userMovieMap = null;
	
	private static Map<Long, Map<Long, Integer>> movieUserMap = null;
	
	static {
		userMap = new HashMap<Long, User>();
		movieMap = new HashMap<Long, Movie>();
		userMovieMap = new HashMap<Long, Map<Long, Integer>>();
		movieUserMap = new HashMap<Long, Map<Long, Integer>>();
		loadUsers();
		loadMovies();
		loadUserMovies();
	}
	
	public static void loadUsers() {
		InputStream in = null;
		BufferedReader reader = null;
		try {
			String path = DataLoader.class.getClassLoader()
					.getResource("trainset/recommend/users.dat").toURI().getPath();
			in = new FileInputStream(new File(path));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				User user = new User();
				StringTokenizer tokenizer = new StringTokenizer(line, "::");
				user.setId(Long.parseLong(tokenizer.nextToken()));
				user.setGender(tokenizer.nextToken());
				user.setAge(Integer.parseInt(tokenizer.nextToken()));
				user.setOccupation(Integer.parseInt(tokenizer.nextToken()));
				user.setZipCode(tokenizer.nextToken());
				userMap.put(user.getId(), user);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}
	
	public static void loadMovies() {
		InputStream in = null;
		BufferedReader reader = null;
		try {
			String path = DataLoader.class.getClassLoader()
					.getResource("trainset/recommend/movies.dat").toURI().getPath();
			in = new FileInputStream(new File(path));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				Movie movie = new Movie();
				StringTokenizer tokenizer = new StringTokenizer(line, "::");
				movie.setId(Long.parseLong(tokenizer.nextToken()));
				movie.setTitle(tokenizer.nextToken());
				movie.setGenres(tokenizer.nextToken());
				movieMap.put(movie.getId(), movie);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}
	
	public static void loadUserMovies() {
		InputStream in = null;
		BufferedReader reader = null;
		try {
			String path = DataLoader.class.getClassLoader()
					.getResource("trainset/recommend/ratings.dat").toURI().getPath();
			in = new FileInputStream(new File(path));
			reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (!("").equals(line) && null != line) {
				UserMovie userMovie = new UserMovie();
				StringTokenizer tokenizer = new StringTokenizer(line, "::");
				userMovie.setUserId(Long.parseLong(tokenizer.nextToken()));
				userMovie.setMovieId(Long.parseLong(tokenizer.nextToken()));
				userMovie.setRating(Integer.parseInt(tokenizer.nextToken()));
				userMovie.setTimestamp(Long.parseLong(tokenizer.nextToken()));
				Map<Long, Integer> movies = userMovieMap.get(userMovie.getUserId());
				if (null == movies) {
					movies = new HashMap<Long, Integer>();
					userMovieMap.put(userMovie.getUserId(), movies);
				}
				movies.put(userMovie.getMovieId(), userMovie.getRating());
				Map<Long, Integer> users = movieUserMap.get(userMovie.getMovieId());
				if (null == users) {
					users = new HashMap<Long, Integer>();
					movieUserMap.put(userMovie.getMovieId(), users);
				}
				users.put(userMovie.getUserId(), userMovie.getRating());
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}
	
	public static Map<Long, User> getUserMap() {
		return userMap;
	}
	
	public static Map<Long, Movie> getMovieMap() {
		return movieMap;
	}
	
	public static Map<Long, Map<Long, Integer>> getUserMovieMap() {
		return userMovieMap;
	}
	
	public static Map<Long, Map<Long, Integer>> getMovieUserMap() {
		return movieUserMap;
	}
	
}
