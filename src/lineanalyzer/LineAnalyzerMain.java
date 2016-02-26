package lineanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

public class LineAnalyzerMain {

	private static HashMap<String, Integer> personMap = new HashMap<>();
	private static String filePath;
	private static String message;
	private static List<String> allLines = Lists.newArrayList();
	private static int totalCount = 0;
	private static int messageCount = 0;
	private static DecimalFormat dFormat = new DecimalFormat("###.##%");

	public static void main(String[] args) {

		try (InputStreamReader inputStreamReader = new InputStreamReader(System.in);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			System.out.println("ファイルパスを入力してください");
			filePath = bufferedReader.readLine();
			readFile(filePath, message);
			viewPersonTalk();
			System.out.println("検索したい文字列を入力してください");
			String message = bufferedReader.readLine();
			readFile(filePath, message);
			System.out.println("「" + message + "」は、" + messageCount + "回使われています");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readFile(String filePath, String message) throws IOException {
		Path path = Paths.get(filePath);
		allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for (String line : allLines) {
			if (message == null) {
				String[] splitWords = line.split("\\t");
				if (splitWords.length >= 3) {
					countPersonTalk(splitWords);
				}
			} else {
				countMessage(message, line);
			}
		}
	}

	private static void countPersonTalk(String[] splitWords) {
		totalCount++;
		if (personMap.get(splitWords[1]) == null) {
			personMap.put(splitWords[1], 1);
		} else {
			personMap.put(splitWords[1], personMap.get(splitWords[1]) + 1);
		}
	}

	private static void viewPersonTalk() {
		System.out.println("トーク履歴内のアカウントごとの発言数");
		double rate = 0;
		for (String key : personMap.keySet()) {
			rate = personMap.get(key) / Double.parseDouble(String.valueOf(totalCount));
			System.out.println(key + " : " + personMap.get(key) + " : " + dFormat.format(rate));
		}
		System.out.println("合計発言数 : " + totalCount);
	}

	private static void countMessage(String message, String read) {
		if (read.indexOf(message) >= 0) {
			messageCount++;
		}
	}
}
