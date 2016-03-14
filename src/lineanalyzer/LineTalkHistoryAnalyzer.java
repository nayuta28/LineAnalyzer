package lineanalyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * LINEのトーク履歴ファイルを解析する.
 * [ファイルのフォーマット]
 * ------------------------------
 * [LINE] [グループ名]のトーク履歴
 * 保存日時：yyyy/mm/dd HH:mm
 * 
 * yyyy/mm/dd(E)
 * HH:mm \t メンバー \t 発言
 * HH:mm \t メンバー \t 発言
 * HH:mm \t メンバー \t 発言
 * 
 * yyyy/mm/dd(E)
 * HH:mm \t メンバー \t 発言
 * HH:mm \t メンバー \t 発言
 * 			・
 * 			・
 * 			・
 * ------------------------------
 * @author nayuta28
 */
public class LineTalkHistoryAnalyzer {

	private static Map<String, Long> memberCountMap = new LinkedHashMap<>();
	private static Long totalTalkCount;

	public static void main(String[] args) {

		try (Scanner scan = new Scanner(System.in)) {

			System.out.println("ファイルパスを入力してください");
			String filePath = scan.nextLine();
			// ファイルから全行を取得
			List<String> allLinesList = readFile(filePath);

			// メンバーごとの発言数をカウント
			countMemberTalk(allLinesList);
			// 結果表示
			outputMemberTalkCount();

			System.out.println("検索したい文字列を入力してください");
			String word = scan.nextLine();
			// 履歴内の単語をカウント
			countWord(word, allLinesList);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> readFile(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		return Files.readAllLines(path, StandardCharsets.UTF_8);
	}

	private static void countMemberTalk(List<String> allLines) {
		// 「時間 \t メンバー \t 発言」となっている行を抽出し、メンバーごとに発言数を集計する
		memberCountMap = allLines.stream().map(s -> s.split("\t")).filter(s -> s.length >= 3)
				.collect(Collectors.groupingBy(s -> s[1], Collectors.counting()));
		// 発言数の昇順でソート
		memberCountMap = sortMemberMap();
		// 合計発言数を集計する
		totalTalkCount = memberCountMap.values().stream().mapToLong(l -> l).sum();
	}

	private static void outputMemberTalkCount() {
		System.out.println("トーク履歴内のアカウントごとの発言数");
		// 「メンバー名：発言数：発言率」で出力
		memberCountMap.forEach((key, value) -> System.out.println(key + " : " + value + " : " + formatRate(value)));
		System.out.println("合計発言数 ： " + totalTalkCount);
	}

	private static void countWord(String word, List<String> allList) {
		// 行内で指定された単語が使われているか集計
		// TODO:同じ行で同単語が使われている場合の集計方法
		long wordCount = allList.stream().filter(s -> s.indexOf(word) > 0).count();
		System.out.println("「" + word + "」は、" + wordCount + "回使われています");
	}

	private static String formatRate(long value) {
		DecimalFormat dFormat = new DecimalFormat("###.##%");
		return dFormat.format(value / Double.parseDouble(String.valueOf(totalTalkCount)));
	}

	private static LinkedHashMap<String, Long> sortMemberMap() {
		LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<String, Long>();
		ArrayList<Entry<String, Long>> entries = new ArrayList<>(memberCountMap.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> e1, Entry<String, Long> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});
		entries.stream().forEach(e -> sortedMap.put(e.getKey(), e.getValue()));
		return sortedMap;
	}
}
