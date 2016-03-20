package lineanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collections;
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

	public static void main(String[] args) {

		try (Scanner scan = new Scanner(System.in)) {

			System.out.println("ファイルパスを入力してください");
			final String filePath = scan.nextLine();
			final List<String> allLinesList = readAllLineFromFile(filePath);

			// メンバーごとの発言数をカウントする
			final Map<String, Long> memberCountMap = countMemberTalk(allLinesList);
			// 合計発言数を集計する
			final long totalCount = countTotalTalk(memberCountMap);
			// 結果を表示する
			outputMemberTalkCount(memberCountMap, totalCount);

			System.out.println("検索したい文字列を入力してください");
			final String word = scan.nextLine();
			// 履歴内の単語をカウント
			countWord(word, allLinesList);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> readAllLineFromFile(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		return Files.readAllLines(path);
	}

	private static Map<String, Long> countMemberTalk(List<String> allLines) {
		Map<String, Long> memberCountMap = new LinkedHashMap<>();
		// 「時間 \t メンバー \t 発言」となっている行を抽出し、メンバーごとに発言数を集計する
		memberCountMap = allLines.stream()
								 .map(s -> s.split("\t"))
								 .filter(s -> s.length >= 3)
								 .collect(Collectors.groupingBy(s -> s[1], Collectors.counting()));
		return memberCountMap;
	}
	
	private static long countTotalTalk(Map<String, Long> memberCountMap) {
		long totalTalkCount = memberCountMap.values().stream()
											.mapToLong(talkCount -> talkCount).sum();
		return totalTalkCount;
	}

	private static void outputMemberTalkCount(Map<String, Long> memberCountMap, long totalTalkCount) {
		System.out.println("トーク履歴内のアカウントごとの発言数");
		// 発言数の降順でソートして集計結果を表示する
		memberCountMap.entrySet()
					  .stream()
					  .sorted(Collections.reverseOrder(Entry.comparingByValue()))
					  .forEach(member -> 
					   System.out.println(String.join(" : ", 
		       		 				  				  member.getKey(),
								 				  	  String.valueOf(member.getValue()),
								 				  	  formatRate(member.getValue(),totalTalkCount))));
		
		System.out.println("合計発言数 ： ".concat(String.valueOf(totalTalkCount)));
	}
	
	private static String formatRate(long memberTalkCount, long totalTalkCount) {
		DecimalFormat dFormat = new DecimalFormat("###.##%");
		return dFormat.format(memberTalkCount / Double.parseDouble(String.valueOf(totalTalkCount)));
	}

	private static void countWord(String word, List<String> allLinesList) {
		// 行内で指定された単語が使われているか集計
		long wordCount = allLinesList.stream().filter(line -> line.indexOf(word) > 0).count();
		System.out.println("「" + word + "」は、" + wordCount + "回使われています");
	}
}
