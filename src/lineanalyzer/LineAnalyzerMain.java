package lineanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;

public class LineAnalyzerMain {

	private static HashMap<String, Integer> personMap = new HashMap<>();
	private static String filePath;
	private static String message;
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
		File file = new File(filePath);
		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);
		String read;

		while (true) {
			read = br.readLine();
			if (read == null) {
				break;
			}

			if (message == null) {
				String[] splitWords = read.split("\\t");
				if (splitWords.length >= 3) {
					countPersonTalk(splitWords);
				}
			} else {
				countMessage(message, read);
			}
		}
		br.close();
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
