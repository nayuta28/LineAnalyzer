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

/**
 * LINEのトーク履歴ファイルを解析する.
 * @author nayuta28
 */
public class LineAnalyzerMain {

	private static String filePath;
	private static String word;
	private static List<String> allLines = Lists.newArrayList();
	private static HashMap<String, Integer> memberMap = new HashMap<>();
	private static int totalCount = 0;
	private static int wordCount = 0;
	
	public static void main(String[] args) {

		try (InputStreamReader inputStreamReader = new InputStreamReader(System.in);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			
			System.out.println("ファイルパスを入力してください");
			filePath = bufferedReader.readLine();

			// メンバーごとの発言数をカウント
			readFile(filePath, word);
			viewPersonTalk();

			// 単語をカウント
			System.out.println("検索したい単語を入力してください");
			String message = bufferedReader.readLine();
			readFile(filePath, message);
			System.out.println("「" + message + "」は、" + wordCount + "回使われています");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readFile(String filePath, String word) throws IOException {
		// ファイル内全行を読み取り
		Path path = Paths.get(filePath);
		allLines = Files.readAllLines(path, StandardCharsets.UTF_8);

		// TODO: 実装方法再検討要
		for (String line : allLines) {
			if (word == null) {
				// 入力されたテキストファイルをタブ区切りに読んでいく
				String[] splitWords = line.split("\\t");
				// 「時間 \t メンバー \t 発言」となっている行のみカウントする
				if (splitWords.length >= 3) {
					countPersonTalk(splitWords);
				}
			} else {
				countWord(word, line);
			}
		}
	}

	private static void countPersonTalk(String[] splitWords) {
		totalCount++;
		if (memberMap.get(splitWords[1]) == null) {
			memberMap.put(splitWords[1], 1);
		} else {
			memberMap.put(splitWords[1], memberMap.get(splitWords[1]) + 1);
		}
	}

	private static void viewPersonTalk() {
		
		System.out.println("トーク履歴内のメンバーごとの発言数");
		
		// メンバーごとの発言数を、 「メンバー名 : 発言数 : 発言率」で表示 
		double rate = 0;
		DecimalFormat dFormat = new DecimalFormat("###.##%");
		for (String key : memberMap.keySet()) {
			rate = memberMap.get(key) / Double.parseDouble(String.valueOf(totalCount));
			System.out.println(key + " : " + memberMap.get(key) + " : " + dFormat.format(rate));
		}
		System.out.println("合計発言数 : " + totalCount);
	}

	private static void countWord(String word, String read) {
		// 
		if (read.indexOf(word) >= 0) {
			wordCount++;
		}
	}
	
	// TODO:マップのソート
	// TODO:月毎の発言数の集計
	// TODO:スタンプ使用率を計算
}
